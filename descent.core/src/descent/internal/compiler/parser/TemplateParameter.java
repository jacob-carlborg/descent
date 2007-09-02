package descent.internal.compiler.parser;

public abstract class TemplateParameter extends ASTDmdNode {
	
	public Loc loc;
	public IdentifierExp ident;
	
	public TemplateParameter(Loc loc, IdentifierExp ident) {
		this.loc = loc;
		this.ident = ident;
	}
	
	public abstract void semantic(Scope sc, SemanticContext context);

	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		// TODO semantic
	}
	
	public TemplateTypeParameter isTemplateTypeParameter() {
		return null;
	}
	
	public TemplateValueParameter isTemplateValueParameter() {
		return null;
	}
	
	public TemplateAliasParameter isTemplateAliasParameter() {
		return null;
	}
	
	public TemplateTupleParameter isTemplateTupleParameter() {
		return null;
	}

}
