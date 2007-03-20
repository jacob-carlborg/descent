package descent.internal.compiler.parser;

public class TemplateTupleParameter extends TemplateParameter {
	
	public TemplateTupleParameter(IdentifierExp ident) {
		super(ident);
	}
	
	@Override
	public int getNodeType() {
		return TEMPLATE_TUPLE_PARAMETER;
	}

}
