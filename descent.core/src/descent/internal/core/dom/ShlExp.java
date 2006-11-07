package descent.internal.core.dom;

public class ShlExp extends BinaryExpression {

	public ShlExp(Loc loc, Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return SHIFT_LEFT;
	}

}
