package descent.internal.compiler.parser;

public class IncrementExp extends AddAssignExp {

	public IncrementExp(Loc loc, Expression e1) {
		super(loc, e1, null);
	}
	
	@Override
	public int getNodeType() {
		return INCREMENT_EXP;
	}

}
