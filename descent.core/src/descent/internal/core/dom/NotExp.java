package descent.internal.core.dom;

public class NotExp extends UnaryExpression {

	public NotExp(Loc loc, Expression e) {
		super(e);
	}
	
	public int getUnaryExpressionType() {
		return NOT;
	}

}
