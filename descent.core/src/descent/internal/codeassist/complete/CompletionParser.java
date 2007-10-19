package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Identifiers;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.ModuleDeclaration;
import descent.internal.compiler.parser.Parser;

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
		if (prevToken.ptr + prevToken.sourceLen == cursorLocation) {
			assistNode = new CompletionOnModuleDeclaration(packages, module, prevToken.ptr);
			return (ModuleDeclaration) assistNode;
		} else if (packages == null && module == null && 
				prevToken.ptr + prevToken.sourceLen <= cursorLocation && cursorLocation <= token.ptr) {
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

}
