package descent.internal.compiler.parser;

public class TemplateInstanceWrapper extends IdentifierExp {

	public TemplateInstance tempinst;

	public TemplateInstanceWrapper(TemplateInstance tempinst) {
		this.tempinst = tempinst;
	}
	
	@Override
	public DYNCAST dyncast() {
		return DYNCAST.DYNCAST_OBJECT;
	}
	
	@Override
	public int getNodeType() {
		return TEMPLATE_INSTANCE_WRAPPER;
	}

}
