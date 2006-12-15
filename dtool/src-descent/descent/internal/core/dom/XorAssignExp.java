package descent.internal.core.dom;


public class XorAssignExp extends BinaryExpression {

	public XorAssignExp(Expression e, Expression exp) {
		super(e, exp);
	}
	
	public int getBinaryExpressionType() {
		return BinaryExpressionTypes.XOR_ASSIGN;
	}

}
