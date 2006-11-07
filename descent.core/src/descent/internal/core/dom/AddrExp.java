package descent.internal.core.dom;

public class AddrExp extends UnaryExpression {
	
	public AddrExp(Loc loc, Expression e) {
		super(e);
	}
	
	public int getUnaryExpressionType() {
		return ADDRESS;
	}

}
