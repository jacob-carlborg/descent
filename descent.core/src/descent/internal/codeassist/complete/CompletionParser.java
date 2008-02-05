package descent.internal.codeassist.complete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.BreakStatement;
import descent.internal.compiler.parser.CaseStatement;
import descent.internal.compiler.parser.Chars;
import descent.internal.compiler.parser.CompoundStatement;
import descent.internal.compiler.parser.ContinueStatement;
import descent.internal.compiler.parser.DebugCondition;
import descent.internal.compiler.parser.DebugSymbol;
import descent.internal.compiler.parser.DotIdExp;
import descent.internal.compiler.parser.ErrorExp;
import descent.internal.compiler.parser.ExpStatement;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.GotoStatement;
import descent.internal.compiler.parser.HashtableOfCharArrayAndObject;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Identifiers;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.ModuleDeclaration;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.Statement;
import descent.internal.compiler.parser.Statements;
import descent.internal.compiler.parser.SuperExp;
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.ThisExp;
import descent.internal.compiler.parser.Token;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeDotIdExp;
import descent.internal.compiler.parser.TypeQualified;
import descent.internal.compiler.parser.Version;
import descent.internal.compiler.parser.VersionCondition;
import descent.internal.compiler.parser.VersionSymbol;

public class CompletionParser extends Parser {
	
	public int cursorLocation;
	private ASTDmdNode assistNode;
	private List<ICompletionOnKeyword> keywordCompletions;
	
	// Javadoc completion, and other ddocs found, in order to
	// provide autocompletion for macros in other places
	// (like in the module declaration)
	private CompletionOnJavadocImpl javadocCompletion;
	private List<char[]> ddocs;
	
	// Versions found in the source file: useful for suggesting a
	// version identifier in a CompletionOnVersionCondition
	public HashtableOfCharArrayAndObject versions;
	public HashtableOfCharArrayAndObject debugs;
	public ASTDmdNode expectedTypeNode;

	public CompletionParser(int apiLevel, char[] source, char[] filename) {
		super(apiLevel, source, 0, source.length, null, null, false, filename);
	}
	
	public ASTDmdNode getAssistNode() {
		return assistNode;
	}
	
	public List<ICompletionOnKeyword> getKeywordCompletions() {
		if (javadocCompletion == null) {
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
			return (Import) assistNode;
		} else {
			return super.newImport(loc, packages, module, aliasid, isstatic);
		}
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
		if (inCompletion()) {		
			assistNode = new CompletionOnGotoStatement(loc, ident);
			return (GotoStatement) assistNode;
		} else {			
			return super.newGotoStatement(loc, ident);
		}
	}
	
	@Override
	protected BreakStatement newBreakStatement(Loc loc, IdentifierExp ident) {
		if (inCompletion()) {
			assistNode = new CompletionOnBreakStatement(loc, ident);
			return (BreakStatement) assistNode;
		} else {			
			return super.newBreakStatement(loc, ident);
		}
	}
	
	@Override
	protected ContinueStatement newContinueStatement(Loc loc, IdentifierExp ident) {
		if (inCompletion()) {
			assistNode = new CompletionOnContinueStatement(loc, ident);
			return (ContinueStatement) assistNode;
		} else {			
			return super.newContinueStatement(loc, ident);
		}
	}
	
	@Override
	protected IdentifierExp newIdentifierExp() {
		if (token.ptr + token.sourceLen == cursorLocation) {
			assistNode = new CompletionOnIdentifierExp(loc, token);
			return (IdentifierExp) assistNode;
		} else {
			// It may be ident.| someOtherIdent
			// So let's see if the cursor is right before the next token,
			// if it's a dot
			Token next = peek(token);
			if (next != null && next.value == TOK.TOKdot && next.ptr + next.sourceLen == cursorLocation) {
				assistNode = new CompletionOnIdentifierExp(loc, token, next.ptr + next.sourceLen);
				return (IdentifierExp) assistNode;
			} else {
				return super.newIdentifierExp();	
			}
		}
	}
	
	@Override
	protected TypeQualified newTypeIdentifier(Loc loc, IdentifierExp id) {
		if (id.start + id.length == cursorLocation) {
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
		if (exp instanceof IdentifierExp) {
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
		if (inCompletion()) {
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
			
			keywordCompletions.add(new CompletionOnKeyword(tokValue, toks));
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
		
		if (inCompletion() && isId) {
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
		
		if (inCompletion() && isId) {
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
		if (caseEnd <= cursorLocation && cursorLocation <= expStart + expLength && exp != null && (exp instanceof ErrorExp || 
				(exp.getNodeType() == ASTDmdNode.IDENTIFIER_EXP))) {
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
		if (prevToken.ptr + prevToken.sourceLen == cursorLocation || token.ptr + token.sourceLen == cursorLocation) {
			assistNode = new CompletionOnDotIdExp(loc, e, id);
			return (DotIdExp) assistNode;
		} else {
			return super.newDotIdExp(loc, e, id);
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
		if (e2 == assistNode) {
			expectedTypeNode = e;
		}
		return super.newAssignExp(loc, e, e2);
	}
	
	private boolean inCompletion() {
		return prevToken.ptr + prevToken.sourceLen <= cursorLocation && cursorLocation <= token.ptr + token.sourceLen
			&& prevToken.value != TOK.TOKdot && prevToken.value != TOK.TOKslice && prevToken.value != TOK.TOKdotdotdot;
	}

}
