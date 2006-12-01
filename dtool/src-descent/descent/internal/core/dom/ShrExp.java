package descent.internal.core.dom;

public class ShrExp extends BinaryExpression {

	public ShrExp(Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return SHIFT_RIGHT;
	}

}
