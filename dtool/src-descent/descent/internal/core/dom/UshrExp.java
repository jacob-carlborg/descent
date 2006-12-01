package descent.internal.core.dom;

public class UshrExp extends BinaryExpression {

	public UshrExp(Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return UNSIGNED_SHIFT_RIGHT;
	}

}
