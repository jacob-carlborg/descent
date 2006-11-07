package descent.internal.core.dom;

public class OrOrExp extends BinaryExpression {

	public OrOrExp(Loc loc, Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return OR_OR;
	}

}
