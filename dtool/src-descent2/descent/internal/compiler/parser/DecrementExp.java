package descent.internal.compiler.parser;

public class DecrementExp extends MinAssignExp {

	public DecrementExp(Loc loc, Expression e1) {
		super(loc, e1, null);
	}
	
	@Override
	public int getNodeType() {
		return DECREMENT_EXP;
	}

}
