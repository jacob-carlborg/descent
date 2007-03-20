package descent.internal.compiler.parser;

public class TemplateAliasParameter extends TemplateParameter {
	
	public Type specAliasT;
	public Type defaultAlias;
	public Dsymbol specAlias;

	public TemplateAliasParameter(IdentifierExp ident, Type specAliasT, Type defaultAlias) {
		super(ident);
		this.specAliasT = specAliasT;
	    this.defaultAlias = defaultAlias;

	    this.specAlias = null;
	}
	
	@Override
	public int getNodeType() {
		return TEMPLATE_ALIAS_PARAMETER;
	}

}
