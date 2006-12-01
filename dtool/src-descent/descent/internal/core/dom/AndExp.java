package descent.internal.core.dom;

public class AndExp extends BinaryExpression {

	public AndExp(Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return AND;
	}

}
