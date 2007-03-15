package descent.internal.compiler.parser;

public class IncrementExp extends AddAssignExp {

	public IncrementExp(Expression e1) {
		super(e1, null);
	}
	
	@Override
	public int kind() {
		return INCREMENT_EXP;
	}

}