package descent.internal.core.dom;

public class InExp extends BinaryExpression {

	public InExp(Loc loc, Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return IN;
	}

}
