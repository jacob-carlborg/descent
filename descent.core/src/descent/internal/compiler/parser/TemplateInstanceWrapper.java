package descent.internal.compiler.parser;

public class TemplateInstanceWrapper extends IdentifierExp {

	public TemplateInstance tempinst;

	public TemplateInstanceWrapper(TemplateInstance tempinst) {
		this.tempinst = tempinst;
	}
	
	@Override
	public int kind() {
		return TEMPLATE_INSTANCE_WRAPPER;
	}

}
