package descent.internal.core.dom;

public class AddExp extends BinaryExpression {

	public AddExp(Loc loc, Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return ADD;
	}

}
