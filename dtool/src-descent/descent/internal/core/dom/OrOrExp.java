package descent.internal.core.dom;


public class OrOrExp extends BinaryExpression {

	public OrOrExp(Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return BinaryExpressionTypes.OR_OR;
	}

}
