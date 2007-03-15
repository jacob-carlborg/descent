package descent.internal.compiler.parser;

public class DecrementExp extends MinAssignExp {

	public DecrementExp(Expression e1) {
		super(e1, null);
	}
	
	@Override
	public int kind() {
		return DECREMENT_EXP;
	}

}
