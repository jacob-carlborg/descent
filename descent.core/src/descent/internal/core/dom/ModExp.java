package descent.internal.core.dom;

public class ModExp extends BinaryExpression {

	public ModExp(Loc loc, Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return MOD;
	}

}
