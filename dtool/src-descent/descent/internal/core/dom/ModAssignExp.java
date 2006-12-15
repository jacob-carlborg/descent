package descent.internal.core.dom;


public class ModAssignExp extends BinaryExpression {

	public ModAssignExp(Expression e, Expression exp) {
		super(e, exp);
	}
	
	public int getBinaryExpressionType() {
		return BinaryExpressionTypes.MOD_ASSIGN;
	}

}
