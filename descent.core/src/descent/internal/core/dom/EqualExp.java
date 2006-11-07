package descent.internal.core.dom;

public class EqualExp extends BinaryExpression {

	public EqualExp(TOK value, Loc loc, Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return EQUAL;
	}

}
