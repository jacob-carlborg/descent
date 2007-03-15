package descent.internal.compiler.parser;

public class TemplateTypeParameter extends TemplateParameter {
	
	public Type specType;
	public Type defaultType;
	
	public TemplateTypeParameter(IdentifierExp ident, Type specType, Type defaultType) {
		super(ident);
		this.specType = specType;
	    this.defaultType = defaultType;
	}
	
	@Override
	public int kind() {
		return TEMPLATE_TYPE_PARAMETER;
	}

}
