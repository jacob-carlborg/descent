package descent.internal.core.dom;

public class IdentityExp extends BinaryExpression {

	public IdentityExp(TOK value, Loc loc, Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return IDENTITY;
	}

}
