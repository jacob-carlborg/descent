package descent.internal.core.dom;

public class ModAssignExp extends BinaryExpression {

	public ModAssignExp(Loc loc, Expression e, Expression exp) {
		super(e, exp);
	}
	
	public int getBinaryExpressionType() {
		return MOD_ASSIGN;
	}

}
