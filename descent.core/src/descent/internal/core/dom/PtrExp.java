package descent.internal.core.dom;

public class PtrExp extends UnaryExpression {

	public PtrExp(Expression e) {
		super(e);
	}
	
	public int getUnaryExpressionType() {
		return POINTER;
	}

}
