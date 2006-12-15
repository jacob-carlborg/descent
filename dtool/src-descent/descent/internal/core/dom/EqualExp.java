package descent.internal.core.dom;


public class EqualExp extends BinaryExpression {

	public EqualExp(TOK value, Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return BinaryExpressionTypes.EQUAL;
	}

}
