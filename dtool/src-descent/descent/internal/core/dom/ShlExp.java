package descent.internal.core.dom;


public class ShlExp extends BinaryExpression {

	public ShlExp(Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return BinaryExpressionTypes.SHIFT_LEFT;
	}

}
