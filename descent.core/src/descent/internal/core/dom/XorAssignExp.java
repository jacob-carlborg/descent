package descent.internal.core.dom;

public class XorAssignExp extends BinaryExpression {

	public XorAssignExp(Loc loc, Expression e, Expression exp) {
		super(e, exp);
	}
	
	public int getBinaryExpressionType() {
		return XOR_ASSIGN;
	}

}
