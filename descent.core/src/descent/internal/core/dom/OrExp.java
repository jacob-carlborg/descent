package descent.internal.core.dom;

public class OrExp extends BinaryExpression {

	public OrExp(Loc loc, Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return OR;
	}

}
