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
import descent.internal.compiler.parser.ContinueStatement;
import descent.internal.compiler.parser.ErrorExp;
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
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeDotIdExp;
import descent.internal.compiler.parser.Version;
import descent.internal.compiler.parser.VersionCondition;
import descent.internal.compiler.parser.VersionSymbol;

public class CompletionParser extends Parser {
	
	public int cursorLocation;
	private ASTDmdNode assistNode;
	private List<ICompletionOnKeyword> keywordCompletions;
	private boolean includeExpectations = true;
	
	// Versions found in the source file: useful for suggesting a
	// version identifier in a CompletionOnVersionCondition
	public HashtableOfCharArrayAndObject versions;

	public CompletionParser(int apiLevel, char[] source) {
		super(apiLevel, source);
	}
	
	public ASTDmdNode getAssistNode() {
		return assistNode;
	}
	
	public List<ICompletionOnKeyword> getKeywordCompletions() {
		if (includeExpectations) {
			return keywordCompletions;
		} else {
			return Collections.EMPTY_LIST;
		}
	}
	
	@Override
	protected ModuleDeclaration newModuleDeclaration(Identifiers packages, IdentifierExp module) {
		int start = CompletionUtils.getFqnStart(packages, module, cursorLocation);
		int end = CompletionUtils.getFqnEnd(packages, module, cursorLocation);
		
		if (start <= cursorLocation && cursorLocation <= end) {
			includeExpectations = false;
			
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
			includeExpectations = false;
			
			assistNode = new CompletionOnImport(loc, packages, module, aliasid, isstatic, cursorLocation);
			return (Import) assistNode;
		} else {
			return super.newImport(loc, packages, module, aliasid, isstatic);
		}
	}

	@Override
	protected Argument newArgument(int storageClass, Type at, IdentifierExp ai, Expression ae) {
		if (inCompletion()) {
			includeExpectations = false;
			
			assistNode = new CompletionOnArgumentName(storageClass, at, ai, ae);
			return (Argument) assistNode;
		} else {
			return super.newArgument(storageClass, at, ai, ae);
		}
	}
	
	@Override
	protected GotoStatement newGotoStatement(Loc loc, IdentifierExp ident) {
		if (inCompletion()) {		
			includeExpectations = false;
			
			assistNode = new CompletionOnGotoStatement(loc, ident);
			return (GotoStatement) assistNode;
		} else {			
			return super.newGotoStatement(loc, ident);
		}
	}
	
	@Override
	protected BreakStatement newBreakStatement(Loc loc, IdentifierExp ident) {
		if (inCompletion()) {
			includeExpectations = false;
			
			assistNode = new CompletionOnBreakStatement(loc, ident);
			return (BreakStatement) assistNode;
		} else {			
			return super.newBreakStatement(loc, ident);
		}
	}
	
	@Override
	protected ContinueStatement newContinueStatement(Loc loc, IdentifierExp ident) {
		if (inCompletion()) {
			includeExpectations = false;
			
			assistNode = new CompletionOnContinueStatement(loc, ident);
			return (ContinueStatement) assistNode;
		} else {			
			return super.newContinueStatement(loc, ident);
		}
	}
	
	@Override
	protected void expect(char[][] toks) {
		// If we are completing already a more important thing, exclude
		// the keyword completion
		if (!includeExpectations) {
			return;
		}
		
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
			includeExpectations = false;
			
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
	protected CaseStatement newCaseStatement(Loc loc, Expression exp, Statement statement, int caseEnd, int expStart, int expLength) {
		// exp.start is -1 if it's an error expression
		if (caseEnd <= cursorLocation && cursorLocation <= expStart + expLength && exp != null && (exp instanceof ErrorExp || 
				(exp.getNodeType() == ASTDmdNode.IDENTIFIER_EXP))) {
			includeExpectations = false;
			
			assistNode = new CompletionOnCaseStatement(loc, exp, statement);
			return (CaseStatement) assistNode;
		} else {
			return super.newCaseStatement(loc, exp, statement, caseEnd, expStart, expLength);
		}
	}
	
	@Override
	protected TypeDotIdExp newTypeDotIdExp(Loc loc, Type t, IdentifierExp exp) {
		if (prevToken.ptr + prevToken.sourceLen == cursorLocation || token.ptr + token.sourceLen == cursorLocation) {
			includeExpectations = false;
			
			assistNode = new CompletionOnTypeDotIdExp(loc, t, exp);
			return (TypeDotIdExp) assistNode;
		} else {
			return super.newTypeDotIdExp(loc, t, exp);
		}
	}
	
	private boolean inCompletion() {
		return prevToken.ptr + prevToken.sourceLen <= cursorLocation && cursorLocation <= token.ptr + token.sourceLen
			&& prevToken.value != TOK.TOKdot && prevToken.value != TOK.TOKslice && prevToken.value != TOK.TOKdotdotdot;
	}

}
