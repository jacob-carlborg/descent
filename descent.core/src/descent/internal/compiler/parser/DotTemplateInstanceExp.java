package descent.internal.compiler.parser;

public class DotTemplateInstanceExp extends UnaExp {

	public TemplateInstance tempinst;

	public DotTemplateInstanceExp(Expression e, TemplateInstance tempinst) {
		super(TOK.TOKdotti, e);
		this.tempinst = tempinst;
	}
	
	@Override
	public int kind() {
		return DOT_TEMPLATE_INSTANCE_EXP;
	}

}
