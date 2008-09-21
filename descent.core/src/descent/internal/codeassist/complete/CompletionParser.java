package descent.internal.codeassist.complete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.AggregateDeclaration;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.BaseClass;
import descent.internal.compiler.parser.BaseClasses;
import descent.internal.compiler.parser.BreakStatement;
import descent.internal.compiler.parser.CaseStatement;
import descent.internal.compiler.parser.Chars;
import descent.internal.compiler.parser.CompoundStatement;
import descent.internal.compiler.parser.ContinueStatement;
import descent.internal.compiler.parser.DebugCondition;
import descent.internal.compiler.parser.DebugSymbol;
import descent.internal.compiler.parser.DotIdExp;
import descent.internal.compiler.parser.ErrorExp;
import descent.internal.compiler.parser.ExpInitializer;
import descent.internal.compiler.parser.ExpStatement;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Expressions;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.GotoStatement;
import descent.internal.compiler.parser.HashtableOfCharArrayAndObject;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Identifiers;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.Initializer;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.ModuleDeclaration;
import descent.internal.compiler.parser.Objects;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.Statement;
import descent.internal.compiler.parser.Statements;
import descent.internal.compiler.parser.SuperExp;
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.TemplateMixin;
import descent.internal.compiler.parser.ThisExp;
import descent.internal.compiler.parser.Token;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeDotIdExp;
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.compiler.parser.TypeQualified;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.compiler.parser.Version;
import descent.internal.compiler.parser.VersionCondition;
import descent.internal.compiler.parser.VersionSymbol;

public class CompletionParser extends Parser {
	
	public int cursorLocation;
	private ASTDmdNode assistNode;
	private List<ICompletionOnKeyword> keywordCompletions;
	private boolean wantKeywords = true;
	private boolean acceptMoreKeywords = true;
	private boolean wantNames = false;
	private boolean wantAssist = true;
	private boolean wantOnlyType = false;
	private boolean wantAssitNode = true;
	
	// Javadoc completion, and other ddocs found, in order to
	// provide autocompletion for macros in other places
	// (like in the module declaration)
	private CompletionOnJavadocImpl javadocCompletion;
	private List<char[]> ddocs;
	
	// Versions found in the source file: useful for suggesting a
	// version identifier in a CompletionOnVersionCondition
	public HashtableOfCharArrayAndObject versions;
	public HashtableOfCharArrayAndObject debugs;
	
	/*
	 * The node to use for computing the expected type, and
	 * the index of the argument being completed in a call.
	 */
	public ASTDmdNode expectedTypeNode;
	public int expectedArgumentIndex;
	
	private ASTDmdNode targetNew;
	
	public boolean inNewExp;
	public boolean inCatExp;
	public boolean isInExp;
	public boolean isInAddrExp;
	public boolean isCallFollowing;
	
	public char[] completionToken;
	public int completionTokenStart;
	public int completionTokenEnd;

	public CompletionParser(int apiLevel, char[] source, char[] filename) {
		super(apiLevel, source, 0, source.length, null, null, false, filename);
		this.diet = true;
	}
	
	/*
	 * Do diet parsing, and return false if the cursor location
	 * falls in a function.
	 */
	@Override
	protected boolean dietParse(FuncDeclaration f) {
		// If any argument is being autocompleted, don't skip
		// Same for return type
		TypeFunction type = (TypeFunction) f.type;
		if (type != null && type.parameters != null) {
			for(Argument arg : type.parameters) {
				if (arg.start <= cursorLocation && cursorLocation <= arg.start + arg.length) {
					return false;
				}
			}
			if (type.next != null && type.next.start <= cursorLocation && cursorLocation <= type.next.start + type.next.length) {
				return false;
			}
		}
		
		int before = token.ptr + token.sourceLen;
		
		boolean ret = super.dietParse(f);
		
		int after = token.ptr + token.sourceLen;
		if (before <= cursorLocation && cursorLocation <= after) {
			return false;
		}
		
		return ret;
	}
	
	public ASTDmdNode getAssistNode() {
		return assistNode;
	}
	
	public boolean wantNames() {
		return wantNames;
	}
	
	public boolean wantAssist() {
		return wantAssist;
	}
	
	public boolean wantOnlyType() {
		return wantOnlyType;
	}
	
	public boolean wantAssitNode() {
		return wantAssitNode && assistNode != null;
	}
	
	public ASTDmdNode getExpectedTypeNode() {
		return expectedTypeNode;
	}
	
	public List<ICompletionOnKeyword> getKeywordCompletions() {
		if (wantKeywords && javadocCompletion == null
				&& !wantOnlyType) {
			return keywordCompletions;
		} else {
			return Collections.EMPTY_LIST;
		}
	}
	
	public CompletionOnJavadocImpl getJavadocCompletion() {
		if (javadocCompletion != null) {
			javadocCompletion.otherDdocs = ddocs;
		}
		return javadocCompletion;
	}
	
	@Override
	protected void inComment() {
		if (token.value == TOK.TOKdocblockcomment
				|| token.value == TOK.TOKdocpluscomment) 
		{
			if (token.ptr <= cursorLocation && cursorLocation <= token.ptr + token.sourceLen) {
				this.javadocCompletion = new CompletionOnJavadocImpl(token.ptr, token.sourceString);
			} else {
				if (ddocs == null) {
					ddocs = new ArrayList<char[]>();
				}
				ddocs.add(token.sourceString);
			}
		}
	}
	
	@Override
	protected ModuleDeclaration newModuleDeclaration(Identifiers packages, IdentifierExp module) {
		int start = CompletionUtils.getFqnStart(packages, module, cursorLocation);
		int end = CompletionUtils.getFqnEnd(packages, module, cursorLocation);
		
		if (start <= cursorLocation && cursorLocation <= end) {
			assistNode = new CompletionOnModuleDeclaration(packages, module, cursorLocation);
			wantKeywords = false;
			return (ModuleDeclaration) assistNode;
		} else {
			return super.newModuleDeclaration(packages, module);
		}
	}
	
	@Override
	protected Import newImport(Loc loc, Identifiers packages, IdentifierExp module, IdentifierExp aliasid, boolean isstatic) { 
		int start = CompletionUtils.getFqnStart(packages, module, cursorLocation);
		int end = CompletionUtils.getFqnEnd(packages, module, cursorLocation);
	
		if (start <= cursorLocation && cursorLocation <= end) {
			assistNode = new CompletionOnImport(loc, packages, module, aliasid, isstatic, cursorLocation);
			wantKeywords = false;
			return (Import) assistNode;
		} else {
			return super.newImport(loc, packages, module, aliasid, isstatic);
		}
	}
	
	@Override
	protected Import addImportAlias(Import s, IdentifierExp name, IdentifierExp alias) {
		super.addImportAlias(s, name, alias);
		
		if (alias != null  && alias.start <= cursorLocation && cursorLocation <= alias.start + alias.length) {
			wantAssist = false;
			wantKeywords = false;
			return s;
		}
		
		if (prevToken.ptr <= cursorLocation && cursorLocation <= token.ptr) {
			CompletionOnImport coi = new CompletionOnImport(s.loc, s.packages, s.id, s.aliasId, s.isstatic, cursorLocation);
			if (name != null && name.start <= cursorLocation && cursorLocation <= name.start + name.length) {
				coi.selectiveName = name;
			}
			coi.isSelective = true;
			assistNode = coi;
			wantKeywords = false;
			return coi;
		}
		
		return s;
	}

	@Override
	protected Argument newArgument(int storageClass, Type at, IdentifierExp ai, Expression ae) {
		if (inCompletion()) {
			assistNode = new CompletionOnArgumentName(storageClass, at, ai, ae);
			return (Argument) assistNode;
		} else {
			return super.newArgument(storageClass, at, ai, ae);
		}
	}
	
	@Override
	protected GotoStatement newGotoStatement(Loc loc, IdentifierExp ident) {
		if (inCompletion() && (prevToken.value != TOK.TOKgoto || prevToken.ptr + prevToken.sourceLen < cursorLocation)) {
			wantKeywords = false;
			
			assistNode = new CompletionOnGotoStatement(loc, ident);
			return (GotoStatement) assistNode;
		} else {			
			return super.newGotoStatement(loc, ident);
		}
	}
	
	@Override
	protected BreakStatement newBreakStatement(Loc loc, IdentifierExp ident) {
		if (inCompletion() && (prevToken.value != TOK.TOKbreak || prevToken.ptr + prevToken.sourceLen < cursorLocation)) {
			wantKeywords = false;
			
			assistNode = new CompletionOnBreakStatement(loc, ident);
			return (BreakStatement) assistNode;
		} else {			
			return super.newBreakStatement(loc, ident);
		}
	}
	
	@Override
	protected ContinueStatement newContinueStatement(Loc loc, IdentifierExp ident) {
		if (inCompletion() && (prevToken.value != TOK.TOKcontinue || prevToken.ptr + prevToken.sourceLen < cursorLocation)) {
			wantKeywords = false;
			
			assistNode = new CompletionOnContinueStatement(loc, ident);
			return (ContinueStatement) assistNode;
		} else {			
			return super.newContinueStatement(loc, ident);
		}
	}
	
	@Override
	protected IdentifierExp newIdentifierExp() {
		if (token.ptr <= cursorLocation && cursorLocation <= token.ptr + token.sourceLen && !wantNames) {
			completionTokenStart = token.ptr;
			completionTokenEnd = token.ptr + token.sourceLen;
			completionToken = CharOperation.subarray(input, completionTokenStart, cursorLocation);
			
			isCallFollowing = peek(token).value == TOK.TOKlparen;
			
			assistNode = new CompletionOnIdentifierExp(loc, token);
			return (IdentifierExp) assistNode;
		} else {
			// It may be ident.| someOtherIdent
			// So let's see if the cursor is right before the next token,
			// if it's a dot
			Token next = peek(token);
			if (next != null && next.value == TOK.TOKdot && next.ptr + next.sourceLen == cursorLocation) {
				assistNode = new CompletionOnIdentifierExp(loc, token, next.ptr + next.sourceLen);
				
				isCallFollowing = next.value == TOK.TOKlparen;
				
				return (IdentifierExp) assistNode;
			} else {
				return super.newIdentifierExp();	
			}
		}
	}
	
	@Override
	protected TypeQualified newTypeIdentifier(Loc loc, IdentifierExp id) {
		if (id.start <= cursorLocation && cursorLocation <= id.start + id.length) {
			assistNode = new CompletionOnTypeIdentifier(loc, id);
			return (TypeQualified) assistNode;
		} else {
			// It may be ident.| someOtherIdent
			// So let's see if the cursor is right before the next token,
			// if it's a dot (the next token is already consumed)
			if (token.value == TOK.TOKdot && token.ptr + token.sourceLen == cursorLocation) {
				assistNode = new CompletionOnTypeIdentifier(loc, id, token.ptr + token.sourceLen);
				return (TypeQualified) assistNode;
			} else {
				return super.newTypeIdentifier(loc, id);
			}
		}
	}
	
	@Override
	protected ThisExp newThisExp(Loc loc) {
		// It may be this.| someOtherIdent
		// So let's see if the cursor is right before the next token,
		// if it's a dot
		Token next = peek(token);
		if (next != null && next.value == TOK.TOKdot && next.ptr + next.sourceLen == cursorLocation) {
			assistNode = new CompletionOnThisDotExp(loc);
			return (ThisExp) assistNode;
		} else {
			return super.newThisExp(loc);
		}
	}
	
	@Override
	protected SuperExp newSuperExp(Loc loc) {
		// It may be this.| someOtherIdent
		// So let's see if the cursor is right before the next token,
		// if it's a dot
		Token next = peek(token);
		if (next != null && next.value == TOK.TOKdot && next.ptr + next.sourceLen == cursorLocation) {
			assistNode = new CompletionOnSuperDotExp(loc);
			return (SuperExp) assistNode;
		} else {
			return super.newSuperExp(loc);
		}
	}
	
	@Override
	protected ExpStatement newExpStatement(Loc loc, Expression exp) {
		if (exp instanceof IdentifierExp && !wantNames) {
			IdentifierExp id = (IdentifierExp) exp;			
			if (id.start + id.length == cursorLocation) {
				assistNode = new CompletionOnExpStatement(loc, exp);
				return (ExpStatement) assistNode;
			} else {
				return super.newExpStatement(loc, exp);	
			}
		} else {
			return super.newExpStatement(loc, exp);	
		}
	}
	
	@Override
	protected void expect(char[][] toks) {
		if (acceptMoreKeywords && inCompletion()) {
			if (keywordCompletions == null) {
				keywordCompletions = new ArrayList<ICompletionOnKeyword>();
			}
			char[] tokValue;
			// Special case for EOF, take the value of the previous token 
			if (token.value == TOK.TOKeof && prevToken.ptr + prevToken.sourceLen == cursorLocation) {
				tokValue = prevToken.toString().toCharArray();
				if (tokValue.length > 0 && (!Chars.isalpha(tokValue[0]) || !Chars.isalpha(tokValue[tokValue.length - 1]))) {
					tokValue = CharOperation.NO_CHAR;
				}
			} else {
				int dif = cursorLocation - token.ptr;
				if (dif < 0) {
					tokValue = CharOperation.NO_CHAR;
				} else {
					tokValue = new char[cursorLocation - token.ptr];
					token.toString().getChars(0, cursorLocation - token.ptr, tokValue, 0);
				}
			}
			
			keywordCompletions.add(new CompletionOnKeyword(tokValue, toks,
					toks == externArgsExpectations || 
					toks == traitsArgsExpectations || 
					toks == scopeArgsExpectations || 
					toks == pragmaArgsExpectations));
			
			if (toks == contractsExpectations) {
				acceptMoreKeywords = false;
			}
			if (toks == scopeArgsExpectations) {
				acceptMoreKeywords = false;
				wantAssitNode = false;
			}
		}
	}
	
	@Override
	protected VersionCondition newVersionCondition(Module module, Loc loc, long level, char[] id) {
		boolean isId = level == 1 && !(id != null && id.length == 1 && id[0] == '1');
		if (isId && id != null) {
			if (versions == null) {
				versions = new HashtableOfCharArrayAndObject();
			}
			versions.put(id, this);
		}
		
		if (inCompletion() && prevToken.ptr + prevToken.sourceLen < cursorLocation && isId) {
			assistNode = new CompletionOnVersionCondition(module, loc, level, id);
			return (VersionCondition) assistNode;
		} else {
			return super.newVersionCondition(module, loc, level, id);
		}
	}
	
	@Override
	protected VersionSymbol newVersionSymbol(Loc loc, IdentifierExp id, Version version) {
		if (id != null && id.ident != null) {
			if (versions == null) {
				versions = new HashtableOfCharArrayAndObject();
			}
			versions.put(id.ident, this);
		}
		return super.newVersionSymbol(loc, id, version);
	}
	
	@Override
	protected DebugCondition newDebugCondition(Module module, Loc loc, long level, char[] id) {
		boolean isId = level == 1 && !(id != null && id.length == 1 && id[0] == '1');
		if (isId && id != null) {
			if (debugs == null) {
				debugs = new HashtableOfCharArrayAndObject();
			}
			debugs.put(id, this);
		}
		
		if (inCompletion() && prevToken.ptr + prevToken.sourceLen < cursorLocation && isId) {
			assistNode = new CompletionOnDebugCondition(module, loc, level, id);
			return (DebugCondition) assistNode;
		} else {
			return super.newDebugCondition(module, loc, level, id);
		}
	}
	
	@Override
	protected DebugSymbol newDebugSymbol(Loc loc, IdentifierExp id, Version version) {
		if (id != null && id.ident != null) {
			if (debugs == null) {
				debugs = new HashtableOfCharArrayAndObject();
			}
			debugs.put(id.ident, this);
		}
		return super.newDebugSymbol(loc, id, version);
	}
	
	@Override
	protected CaseStatement newCaseStatement(Loc loc, Expression exp, Statement statement, int caseEnd, int expStart, int expLength) {
		// exp.start is -1 if it's an error expression
		if (caseEnd < cursorLocation && cursorLocation <= expStart + expLength && exp != null && (exp instanceof ErrorExp || 
				(exp.getNodeType() == ASTDmdNode.IDENTIFIER_EXP))) {
			wantKeywords = false;
			
			assistNode = new CompletionOnCaseStatement(loc, exp, statement);
			return (CaseStatement) assistNode;
		} else {
			return super.newCaseStatement(loc, exp, statement, caseEnd, expStart, expLength);
		}
	}
	
	@Override
	protected TypeDotIdExp newTypeDotIdExp(Loc loc, Type t, IdentifierExp exp) {
		if (prevToken.ptr + prevToken.sourceLen == cursorLocation || token.ptr + token.sourceLen == cursorLocation) {
			assistNode = new CompletionOnTypeDotIdExp(loc, t, exp);
			return (TypeDotIdExp) assistNode;
		} else {
			return super.newTypeDotIdExp(loc, t, exp);
		}
	}
	
	@Override
	protected DotIdExp newDotIdExp(Loc loc, Expression e, IdentifierExp id) {
		Token pivot;
		if (prevToken.ptr <= cursorLocation && cursorLocation <= prevToken.ptr + prevToken.sourceLen) {
			pivot = prevToken;
		} else if (token.ptr <= cursorLocation && cursorLocation <= token.ptr + token.sourceLen) {
			pivot = token;
		} else if (e.start + e.length < cursorLocation && cursorLocation < id.start) {
			assistNode = new CompletionOnDotIdExp(loc, e, new IdentifierExp(CharOperation.NO_CHAR));
			return (DotIdExp) assistNode;
		} else {
			return super.newDotIdExp(loc, e, id);
		}
		
		if (cursorLocation <= id.start) {
			assistNode = new CompletionOnDotIdExp(loc, e, new IdentifierExp(CharOperation.NO_CHAR));
			return (DotIdExp) assistNode;
		} else {
			if (pivot.value == TOK.TOKdot) {
				completionTokenStart = pivot.ptr + pivot.sourceLen;
				completionTokenEnd = pivot.ptr + pivot.sourceLen;
				completionToken = CharOperation.subarray(input, completionTokenStart, cursorLocation);
			} else {
				completionTokenStart = pivot.ptr;
				completionTokenEnd = pivot.ptr + pivot.sourceLen;
				completionToken = CharOperation.subarray(input, completionTokenStart, cursorLocation);
			}
			
			assistNode = new CompletionOnDotIdExp(loc, e, id);
			return (DotIdExp) assistNode;
		}
	}
	
	@Override
	protected Type newTypeBasicForCurrentToken() {
		Type typeBasic = super.newTypeBasicForCurrentToken();
		if (token.ptr + token.sourceLen == cursorLocation) {
			IdentifierExp identifierExp = new IdentifierExp(typeBasic.toCharArray());
			identifierExp.copySourceRange(typeBasic);
			typeBasic = new CompletionOnTypeIdentifier(loc, identifierExp);
			assistNode = typeBasic;
		}
		return typeBasic;
	}
	
	@Override
	protected TemplateMixin newTemplateMixin(Loc loc, IdentifierExp id, Type tqual, Identifiers idents, Objects tiargs) {
		if (prevToken.ptr + prevToken.sourceLen <= cursorLocation && cursorLocation <= token.ptr) {
			wantKeywords = false;
			
			assistNode = new CompletionOnTemplateMixin(loc, id, tqual, idents, tiargs, encoder);
			return (CompletionOnTemplateMixin) assistNode;
		}
		return super.newTemplateMixin(loc, id, tqual, idents, tiargs);
	}
	
	@Override
	protected CompoundStatement newBlock(Statements statements, int start, int length) {
		if (assistNode == null && start <= cursorLocation && cursorLocation <= start + length) {
			CompoundStatement cs = new CompletionOnCompoundStatement(loc, statements);
			cs.setSourceRange(start, length);
			return (CompoundStatement) (assistNode = cs);
		} else {
			return super.newBlock(statements, start, length);
		}
	}
	
	@Override
	protected Expression newAssignExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newAssignExp(loc, e, e2);
	}
	
	@Override
	protected Expression newAddAssignExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newAddAssignExp(loc, e, e2);
	}
	
	@Override
	protected Expression newAndAssignExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newAndAssignExp(loc, e, e2);
	}
	
	@Override
	protected Expression newCatAssignExp(Loc loc, Expression e, Expression e2) {
		boolean result = analyzeBinExp(e, e2);
		if (result) {
			inCatExp = true;
		}
		
		return super.newCatAssignExp(loc, e, e2);
	}
	
	@Override
	protected Expression newDivAssignExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newDivAssignExp(loc, e, e2);
	}
	
	@Override
	protected Expression newMinAssignExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newMinAssignExp(loc, e, e2);
	}
	
	@Override
	protected Expression newModAssignExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newModAssignExp(loc, e, e2);
	}
	
	@Override
	protected Expression newMulAssignExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newMulAssignExp(loc, e, e2);
	}
	
	@Override
	protected Expression newOrAssignExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newOrAssignExp(loc, e, e2);
	}
	
	@Override
	protected Expression newShlAssignExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newShlAssignExp(loc, e, e2);
	}
	
	@Override
	protected Expression newShrAssignExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newShrAssignExp(loc, e, e2);
	}
	
	@Override
	protected Expression newUshrAssignExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newUshrAssignExp(loc, e, e2);
	}
	
	@Override
	protected Expression newXorAssignExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newXorAssignExp(loc, e, e2);
	}
	
	@Override
	protected Expression newAddExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newAddExp(loc, e, e2);
	}
	
	@Override
	protected Expression newAndAndExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newAndAndExp(loc, e, e2);
	}
	
	@Override
	protected Expression newAndExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newAndExp(loc, e, e2);
	}
	
	@Override
	protected Expression newCatExp(Loc loc, Expression e, Expression e2) {
		boolean result = analyzeBinExp(e, e2);
		if (result) {
			inCatExp = true;
		}
		
		return super.newCatExp(loc, e, e2);
	}
	
	@Override
	protected Expression newCmpExp(Loc loc, TOK op, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newCmpExp(loc, op, e, e2);
	}
	
	@Override
	protected Expression newDivExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newDivExp(loc, e, e2);
	}
	
	@Override
	protected Expression newEqualExp(Loc loc, TOK op, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newEqualExp(loc, op, e, e2);
	}
	
	@Override
	protected Expression newIdentityExp(Loc loc, TOK op, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newIdentityExp(loc, op, e, e2);
	}
	
	@Override
	protected Expression newInExp(Loc loc, Expression e, Expression e2) {
		boolean match = analyzeBinExp(e, e2);
		if (match) {
			isInExp = true;
		}
		
		return super.newInExp(loc, e, e2);
	}
	
	@Override
	protected Expression newMinExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newMinExp(loc, e, e2);
	}
	
	@Override
	protected Expression newModExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newModExp(loc, e, e2);
	}
	
	@Override
	protected Expression newMulExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newMulExp(loc, e, e2);
	}
	
	@Override
	protected Expression newOrExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newOrExp(loc, e, e2);
	}
	
	@Override
	protected Expression newOrOrExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newOrOrExp(loc, e, e2);
	}
	
	@Override
	protected Expression newShlExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newShlExp(loc, e, e2);
	}
	
	@Override
	protected Expression newShrExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newShrExp(loc, e, e2);
	}
	
	@Override
	protected Expression newUshrExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newUshrExp(loc, e, e2);
	}
	
	@Override
	protected Expression newXorExp(Loc loc, Expression e, Expression e2) {
		analyzeBinExp(e, e2);
		return super.newXorExp(loc, e, e2);
	}
	
	@Override
	protected Expression newCondExp(Loc loc, Expression e, Expression e1, Expression e2) {
		if (e1.start + e1.length <= cursorLocation && 
				cursorLocation <= e2.start + e2.length) {
			expectedTypeNode = e1;
		}
		return super.newCondExp(loc, e, e1, e2);
	}
	
	@Override
	protected Expression newAddrExp(Loc loc, Expression e) {
		if (e == assistNode) {
			isInAddrExp = true;
		}
		return super.newAddrExp(loc, e);
	}
	
	@Override
	protected VarDeclaration newVarDeclaration(Loc loc, Type type, IdentifierExp ident, Initializer init) {
		VarDeclaration var = super.newVarDeclaration(loc, type, ident, init);
		if (init instanceof ExpInitializer) {
			Expression exp = ((ExpInitializer) init).exp;
			if (isMatch(ident, exp)) {
				expectedTypeNode = var;
			}
		}
		
		// Case:
		// int foo = |
		if (((ident == null && prevToken.ptr + prevToken.sourceLen < cursorLocation) ||
				(ident != null && prevToken.ptr + prevToken.sourceLen <= cursorLocation) )
				&& cursorLocation <= token.ptr &&
				(init == null || (init instanceof ExpInitializer && ((ExpInitializer) init).exp instanceof ErrorExp))) {
			if (ident == null) {
				wantNames = true;
				assistNode = var;
			} else {
				CompletionOnIdentifierExp comp = new CompletionOnIdentifierExp(loc, new IdentifierExp(CharOperation.NO_CHAR));
				assistNode = comp;			
				var.init = new ExpInitializer(loc, comp);
			}
		}
		
		if (ident instanceof CompletionOnIdentifierExp) {
			wantNames = true;
			assistNode = var;
		} else if (ident == null && token.value == TOK.TOKidentifier && cursorLocation == token.ptr + token.sourceLen) {
			wantNames = true;
			var = new VarDeclaration(loc, type, new IdentifierExp(token.sourceString), init);
			assistNode = var;
		}
		
		return var;
	}
	
	@Override
	protected Expression newCallExp(Loc loc, Expression e, Expressions expressions) {
		Expression callExp = super.newCallExp(loc, e, expressions);
		
		if (expressions != null) {
			if (expressions.isEmpty()) {
				if (e.start + e.length <= cursorLocation && cursorLocation <= token.ptr
						&& prevToken.value == TOK.TOKrparen) {
					assistNode = new CompletionOnCallExp(loc, e, expressions);
					return (Expression) assistNode;
				}
			} else {
				for (int i = 0; i < expressions.size(); i++) {
					Expression exp = expressions.get(i);
					if (isMatch(exp)) {
						expectedTypeNode = callExp;
						expectedArgumentIndex = i;
						
						if (cursorLocation == exp.start) {
							assistNode = new CompletionOnCallExp(loc, e, expressions);
							return (Expression) assistNode;
						}
					}
				}
			}
		}
		
		if (prevToken.ptr + prevToken.sourceLen <= cursorLocation && cursorLocation <= token.ptr) {
			assistNode = new CompletionOnCallExp(loc, e, expressions);
			return (Expression) assistNode;
		}
		
		return callExp;
	}
	
	@Override
	protected Expression newNewExp(Loc loc, Expression thisexp, Expressions newargs, Type t, Expressions arguments, int start) {
		Expression newExp = super.newNewExp(loc, thisexp, newargs, t, arguments, start);
		
		if ((t != null && isMatch(t)) || (thisexp != null && isMatch(thisexp))
			|| (prevToken.ptr + prevToken.sourceLen < cursorLocation && cursorLocation <= token.ptr)
			|| (start <= cursorLocation && t != null && cursorLocation <= t.start)) {
			inNewExp = true;
			targetNew = newExp;
		}
		
		int newArgsSize = newargs == null ? 0 : newargs.size();
		if (newargs != null) {
			for (int i = 0; i < newArgsSize; i++) {
				Expression exp = newargs.get(i);
				if (isMatch(exp)) {
					expectedTypeNode = newExp;
					expectedArgumentIndex = i;
				}
			}
		}
		
		if (arguments != null) {
			if (arguments.isEmpty()) {
				if (((thisexp != null && thisexp.start + thisexp.length <= cursorLocation) || 
					(t != null && t.start + t.length <= cursorLocation)) && cursorLocation <= token.ptr
						&& prevToken.value == TOK.TOKrparen) {
					assistNode = new CompletionOnNewExp(loc, thisexp, newargs, t, arguments);
					return (Expression) assistNode;
				}
			} else {
				for (int i = 0; i < arguments.size(); i++) {
					Expression exp = arguments.get(i);
					if (isMatch(exp)) {
						expectedTypeNode = newExp;
						expectedArgumentIndex = i + newArgsSize;
						
						if (cursorLocation == exp.start) {
							assistNode = new CompletionOnNewExp(loc, thisexp, newargs, t, arguments);
							return (Expression) assistNode;
						}
					}
				}
			}
		}
		
		return newExp;
	}
	
	@Override
	protected Statement newReturnStatement(Loc loc, Expression exp) {
		if (isMatch(exp) || 0 <= cursorLocation - prevToken.ptr && cursorLocation - prevToken.ptr <= 3) {
			CompletionOnReturnStatement ret = new CompletionOnReturnStatement(loc, exp);
			expectedTypeNode = ret;
			return ret;
		} else {
			return super.newReturnStatement(loc, exp);
		}
	}
	
	@Override
	protected AggregateDeclaration newClassDeclaration(Loc loc, IdentifierExp id, BaseClasses baseClasses) {
		// We don't want assist for an aggregate's name, but we do want it
		// for base classes
		if (prevToken.ptr + prevToken.sourceLen <= cursorLocation && cursorLocation <= token.ptr && 
				(prevToken.value == TOK.TOKcolon || prevToken.value == TOK.TOKcomma)) {
			wantOnlyType = true;
			
			assistNode = new CompletionOnClassDeclaration(loc, id, baseClasses);
			return (AggregateDeclaration) assistNode;
		}
		
		// If it's class NOT_IDENTIFIER and the cursor is after class,
		// NOT inside it, we don't want assist
		if (prevToken.value == TOK.TOKclass && token.value != TOK.TOKidentifier
				&& cursorLocation > prevToken.ptr + prevToken.sourceLen) {
			this.wantAssist = false;
			return super.newClassDeclaration(loc, id, baseClasses);
		}
		
		if (baseClasses != null) {
			int i = 0;
			for(BaseClass bc : baseClasses) {
				if (bc.type != null && bc.type.start <= cursorLocation && cursorLocation <= bc.type.start + bc.type.length) {
					wantOnlyType = true;
					
					assistNode = new CompletionOnClassDeclaration(loc, id, baseClasses);
					((CompletionOnClassDeclaration) assistNode).baseClassIndex = i;
					return (AggregateDeclaration) assistNode;
				}
				i++;
			}
		}
		
		return super.newClassDeclaration(loc, id, baseClasses);
	}
	
	@Override
	protected AggregateDeclaration endAggregateDeclaration(AggregateDeclaration a) {
//		if (a instanceof ClassDeclaration && a.start < cursorLocation && cursorLocation < a.start + a.length) {
//			ClassDeclaration original = (ClassDeclaration) a;
//			CompletionOnClassDeclaration cc = new CompletionOnClassDeclaration(original.loc, original.ident, original.baseclasses);
//			cc.members = original.members;
//			cc.sourceMembers = original.sourceMembers;
//			cc.isCompletingScope = true;
//			
//			assistNode = cc;
//			return cc;
//		}
		return super.endAggregateDeclaration(a);
	}
	
	@Override
	protected AggregateDeclaration newInterfaceDeclaration(Loc loc, IdentifierExp id, BaseClasses baseClasses) {
		// We don't want assist for an aggregate's name, but we do want it
		// for base classes
		if (prevToken.ptr + prevToken.sourceLen <= cursorLocation && cursorLocation <= token.ptr && 
				(prevToken.value == TOK.TOKcolon || prevToken.value == TOK.TOKcomma)) {
			wantOnlyType = true;
			
			assistNode = new CompletionOnInterfaceDeclaration(loc, id, baseClasses);
			return (AggregateDeclaration) assistNode;
		}
		
		// If it's interface NOT_IDENTIFIER and the cursor is after class, 
		// NOT inside it, we don't want assist
		if (prevToken.value == TOK.TOKinterface && token.value != TOK.TOKidentifier
				&& cursorLocation > prevToken.ptr + prevToken.sourceLen) {
			this.wantAssist = false;
			return super.newClassDeclaration(loc, id, baseClasses);
		}
		
		if (baseClasses != null) {
			int i = 0;
			for(BaseClass bc : baseClasses) {
				if (bc.type != null && bc.type.start <= cursorLocation && cursorLocation <= bc.type.start + bc.type.length) {
					wantOnlyType = true;
					
					assistNode = new CompletionOnInterfaceDeclaration(loc, id, baseClasses);
					((CompletionOnInterfaceDeclaration) assistNode).baseClassIndex = i;
					return (AggregateDeclaration) assistNode;
				}
				i++;
			}
		}
		
		return super.newInterfaceDeclaration(loc, id, baseClasses);
	}
	
	@Override
	protected AggregateDeclaration newStructDeclaration(Loc loc, IdentifierExp id) {
		// We don't want assist for an aggregate's name
		if (prevToken.ptr + prevToken.sourceLen < cursorLocation && cursorLocation <= token.ptr) {
			wantAssist = false;
		}
		
		return super.newStructDeclaration(loc, id);
	}
	
	@Override
	protected AggregateDeclaration newUnionDeclaration(Loc loc, IdentifierExp id) {
		// We don't want assist for an aggregate's name
		if (prevToken.ptr + prevToken.sourceLen < cursorLocation && cursorLocation <= token.ptr) {
			wantAssist = false;
		}
		
		return super.newUnionDeclaration(loc, id);
	}
	
	@Override
	public TOK nextToken() {
		// If the cursor is located inside the current token
		if (token.ptr <= cursorLocation && cursorLocation <= token.ptr + token.sourceLen && !wantNames) {
			// Anything that's a word (not a symbol) will aid the autocompletion
			switch(token.value) {
			case TOKabstract:
			case TOKalias:
			case TOKalign:
			case TOKasm:
			case TOKauto:
			case TOKbit:
			case TOKbody:
			case TOKbool:
			case TOKbreak:
			case TOKcase:
			case TOKcast:
			case TOKcatch:
			case TOKcent:
			case TOKchar:
			case TOKclass:
			case TOKconst:
			case TOKcontinue:
			case TOKdchar:
			case TOKdebug:
			case TOKdefault:
			case TOKdelegate:
			case TOKdelete:
			case TOKdeprecated:
			case TOKdo:
			case TOKelse:
			case TOKenum:
			case TOKexport:
			case TOKextern:
			case TOKfalse:
			case TOKfinal:
			case TOKfinally:
			case TOKfloat32:
			case TOKfloat64:
			case TOKfloat80:
			case TOKfor:
			case TOKforeach:
			case TOKforeach_reverse:
			case TOKgoto:
			case TOKidentifier:
			case TOKif:
			case TOKiftype:
			case TOKimaginary32:
			case TOKimaginary64:
			case TOKimaginary80:
			case TOKimport:
			case TOKin:
			case TOKint16:
			case TOKint32:
			case TOKint64:
			case TOKint8:
			case TOKinterface:
			case TOKlazy:
			case TOKmacro:
			case TOKmixin:
			case TOKmodule:
			case TOKnew:
			case TOKnothrow:
			case TOKnull:
			case TOKon_scope_exit:
			case TOKon_scope_failure:
			case TOKon_scope_success:
			case TOKout:
			case TOKoverride:
			case TOKpackage:
			case TOKpragma:
			case TOKprivate:
			case TOKprotected:
			case TOKpublic:
			case TOKpure:
			case TOKref:
			case TOKreturn:
			case TOKscope:
			case TOKstatic:
			case TOKstruct:
			case TOKsuper:
			case TOKswitch:
			case TOKsynchronized:
			case TOKtemplate:
			case TOKthis:
			case TOKthrow:
			case TOKtraits:
			case TOKtrue:
			case TOKtry:
			case TOKtypedef:
			case TOKtypeid:
			case TOKtypeof:
			case TOKucent:
			case TOKunion:
			case TOKuns16:
			case TOKuns32:
			case TOKuns64:
			case TOKuns8:
			case TOKversion:
			case TOKvoid:
			case TOKvolatile:
			case TOKwchar:
			case TOKwhile:
			case TOKwith:
				completionTokenStart = token.ptr;
				completionTokenEnd = token.ptr + token.sourceLen;
				completionToken = CharOperation.subarray(input, completionTokenStart, cursorLocation);
				break;
			default:
				break;
			}
		}
		return super.nextToken();
	}
	
	private boolean analyzeBinExp(Expression e, Expression e2) {
		if (isMatch(e, e2)) {
			expectedTypeNode = e;
			return true;
		}
		return false;
	}
	
	private boolean isMatch(ASTDmdNode second) {
		return isMatch(null, second);
	}
	
	private boolean isMatch(ASTDmdNode first, ASTDmdNode second) {
		if (expectedTypeNode != null) {
			return false;
		}
		
		if (second == assistNode ||
				(second != null && second.start <= cursorLocation && cursorLocation <= second.start + second.length) ||
				(first != null && second != null && first.start + first.length <= cursorLocation && cursorLocation <= second.start) ||
				second == targetNew) {
			return true;
		}
		
		return false;
	}
	
	private boolean inCompletion() {
		return prevToken.ptr + prevToken.sourceLen <= cursorLocation && cursorLocation <= token.ptr + token.sourceLen
			&& prevToken.value != TOK.TOKdot && prevToken.value != TOK.TOKslice && prevToken.value != TOK.TOKdotdotdot;
	}

}
