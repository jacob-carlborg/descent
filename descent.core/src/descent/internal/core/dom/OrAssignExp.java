package descent.internal.core.dom;

public class OrAssignExp extends BinaryExpression {

	public OrAssignExp(Expression e, Expression exp) {
		super(e, exp);
	}
	
	public int getBinaryExpressionType() {
		return OR_ASSIGN;
	}

}
