package descent.internal.core.dom;

public class CommaExp extends BinaryExpression {

	public CommaExp(Loc loc, Expression e, Expression exp) {
		super(e, exp);
	}
	
	public int getBinaryExpressionType() {
		return COMMA;
	}

}
