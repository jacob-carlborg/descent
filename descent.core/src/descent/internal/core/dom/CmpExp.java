package descent.internal.core.dom;

public class CmpExp extends BinaryExpression {

	public CmpExp(TOK op, Loc loc, Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return CMP;
	}

}
