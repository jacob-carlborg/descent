package descent.internal.core.dom;

public class ComExp extends UnaryExpression {

	public ComExp(Loc loc, Expression e) {
		super(e);
	}
	
	public int getUnaryExpressionType() {
		return INVERT;
	}

}
