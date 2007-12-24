package descent.internal.compiler.parser;

public interface IModuleDeclaration extends INode {
	
	IdentifierExp id();
	
	Identifiers packages();

}
