package descent.internal.core.dom;

public class AndAndExp extends BinaryExpression {

	public AndAndExp(Loc loc, Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return AND_AND;
	}

}
