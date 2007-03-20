package descent.internal.compiler.parser;

public class TemplateValueParameter extends TemplateParameter {
	
	public Type valType;
	public Expression specValue;
	public Expression defaultValue;
	
	public TemplateValueParameter(IdentifierExp ident, Type valType, Expression specValue, Expression defaultValue) {
		super(ident);
		this.valType = valType;
		this.specValue = specValue;
		this.defaultValue = defaultValue;
	}
	
	@Override
	public int getNodeType() {
		return TEMPLATE_VALUE_PARAMETER;
	}

}
