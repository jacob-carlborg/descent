package descent.internal.core.dom;

public class DivExp extends BinaryExpression {

	public DivExp(Loc loc, Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return DIV;
	}

}
