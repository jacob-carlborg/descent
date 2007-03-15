package descent.internal.compiler.parser;

public abstract class TemplateParameter extends ASTNode {
	
	public IdentifierExp ident;
	
	public TemplateParameter(IdentifierExp ident) {
		this.ident = ident;
	}

}
