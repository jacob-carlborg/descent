package descent.internal.core.dom;

public class MulExp extends BinaryExpression {

	public MulExp(Loc loc, Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return MUL;
	}

}
