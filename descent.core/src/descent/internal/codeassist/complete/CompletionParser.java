package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Identifiers;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.ModuleDeclaration;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.Type;

public class CompletionParser extends Parser {
	
	public int cursorLocation;
	private ASTDmdNode assistNode;

	public CompletionParser(int apiLevel, char[] source) {
		super(apiLevel, source);
	}
	
	public ASTDmdNode getAssistNode() {
		return assistNode;
	}
	
	@Override
	protected ModuleDeclaration newModuleDeclaration(Identifiers packages, IdentifierExp module) {
		int start = CompletionOnModuleDeclaration.getModuleNameStart(packages, module, cursorLocation);
		int end = CompletionOnModuleDeclaration.getModuleNameEnd(packages, module, cursorLocation);
		
		if (start <= cursorLocation && cursorLocation <= end) {
			assistNode = new CompletionOnModuleDeclaration(packages, module, cursorLocation);
			return (ModuleDeclaration) assistNode;
		} else {
			return super.newModuleDeclaration(packages, module);
		}
	}
	
	@Override
	protected Import newImport(Loc loc, Identifiers packages, IdentifierExp module, IdentifierExp aliasid, boolean isstatic) {
		if (prevToken.ptr + prevToken.sourceLen == cursorLocation) {
			assistNode = new CompletionOnImport(loc, packages, module, aliasid, isstatic);
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

}
