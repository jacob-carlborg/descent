package descent.internal.compiler.parser;

public class TypeInstance extends TypeQualified {

	public TemplateInstance tempinst;

	public TypeInstance(Loc loc, TemplateInstance tempinst) {
		super(loc, TY.Tinstance);
		this.tempinst = tempinst;
	}
	
	@Override
	public int getNodeType() {
		return TYPE_INSTANCE;
	}

}
