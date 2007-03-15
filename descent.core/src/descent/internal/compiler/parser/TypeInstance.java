package descent.internal.compiler.parser;

public class TypeInstance extends TypeQualified {

	public TemplateInstance tempinst;

	public TypeInstance(TemplateInstance tempinst) {
		super(TY.Tinstance);
		this.tempinst = tempinst;
	}
	
	@Override
	public int kind() {
		return TYPE_INSTANCE;
	}

}
