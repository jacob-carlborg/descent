package descent.internal.codeassist.complete;

import java.util.ArrayList;
import java.util.List;

import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.Chars;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Identifiers;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.ModuleDeclaration;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.Type;

public class CompletionParser extends Parser {
	
	public int cursorLocation;
	private ASTDmdNode assistNode;
	private List<ICompletionOnKeyword> keywordCompletions;

	public CompletionParser(int apiLevel, char[] source) {
		super(apiLevel, source);
	}
	
	public ASTDmdNode getAssistNode() {
		return assistNode;
	}
	
	public List<ICompletionOnKeyword> getKeywordCompletions() {
		return keywordCompletions;
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
		if (prevToken.ptr + prevToken.sourceLen <= cursorLocation && cursorLocation <= token.ptr) {
			assistNode = new CompletionOnArgumentName(storageClass, at, ai, ae);
			return (Argument) assistNode;
		} else {
			return super.newArgument(storageClass, at, ai, ae);
		}
	}
	
	@Override
	protected void expect(char[][] toks) {
		if (prevToken.ptr + prevToken.sourceLen <= cursorLocation && cursorLocation <= token.ptr + token.sourceLen) {
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

}
