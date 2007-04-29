package descent.internal.compiler.parser;

public class XorAssignExp extends BinExp {

	public XorAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKxorass, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return XOR_ASSIGN_EXP;
	}

}
