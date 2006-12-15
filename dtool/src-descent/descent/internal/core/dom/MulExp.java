package descent.internal.core.dom;


public class MulExp extends BinaryExpression {

	public MulExp(Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return BinaryExpressionTypes.MUL;
	}

}
