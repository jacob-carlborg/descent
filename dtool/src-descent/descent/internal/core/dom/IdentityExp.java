package descent.internal.core.dom;

public class IdentityExp extends BinaryExpression {

	private final TOK value;

	public IdentityExp(TOK value, Expression e, Expression e2) {
		super(e, e2);
		this.value = value;
	}
	
	public int getBinaryExpressionType() {
		switch(value) {
		case TOKidentity: return IDENTITY;
		case TOKnotidentity: return NOT_IDENTITY;
		}
		return 0;
	}

}