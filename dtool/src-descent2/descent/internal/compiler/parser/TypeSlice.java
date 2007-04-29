package descent.internal.compiler.parser;

public class TypeSlice extends Type {
	
	public Expression lwr;
	public Expression upr;
	
	public TypeSlice(Type next, Expression lwr, Expression upr) {
		super(TY.Tslice, next);
		this.lwr = lwr;
		this.upr = upr;
	}
	
	@Override
	public int getNodeType() {
		return TYPE_SLICE;
	}

}
