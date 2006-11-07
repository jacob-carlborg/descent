package descent.internal.core.dom;

public class NegExp extends UnaryExpression {

	public NegExp(Loc loc, Expression e) {
		super(e);
	}
	
	public int getUnaryExpressionType() {
		return NEGATIVE;
	}

}
