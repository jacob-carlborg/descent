package descent.internal.core.dom;


public class CommaExp extends BinaryExpression {

	public CommaExp(Expression e, Expression exp) {
		super(e, exp);
	}
	
	public int getBinaryExpressionType() {
		return BinaryExpressionTypes.COMMA;
	}

}
