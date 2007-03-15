package descent.internal.compiler.parser;

public class TemplateTupleParameter extends TemplateParameter {
	
	public TemplateTupleParameter(IdentifierExp ident) {
		super(ident);
	}
	
	@Override
	public int kind() {
		return TEMPLATE_TUPLE_PARAMETER;
	}

}
