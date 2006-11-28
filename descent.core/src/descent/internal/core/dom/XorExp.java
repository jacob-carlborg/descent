package descent.internal.core.dom;

public class XorExp extends BinaryExpression {

	public XorExp(Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return XOR;
	}

}
