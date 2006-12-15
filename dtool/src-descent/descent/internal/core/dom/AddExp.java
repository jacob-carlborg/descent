package descent.internal.core.dom;


public class AddExp extends BinaryExpression {

	public AddExp(Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return BinaryExpressionTypes.ADD;
	}

}
