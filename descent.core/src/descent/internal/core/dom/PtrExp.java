package descent.internal.core.dom;

public class PtrExp extends UnaryExpression {

	public PtrExp(Loc loc, Expression e) {
		super(e);
	}
	
	public int getUnaryExpressionType() {
		return POINTER;
	}

}
