package descent.internal.compiler.parser;

public class TypeAArray extends Type {
	
	public Type index;
	public Type key;
	
	public TypeAArray(Type t, Type index) {
		super(TY.Taarray, t);
		this.index = index;
		this.key = null;
	}
	
	@Override
	public Expression defaultInit(SemanticContext context) {
		Expression e;
	    e = new NullExp();
	    e.type = this;
	    return e;
	}
	
	@Override
	public int getNodeType() {
		return TYPE_A_ARRAY;
	}

}
