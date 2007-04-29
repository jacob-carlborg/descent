package descent.internal.compiler.parser;

public class XorExp extends BinExp {

	public XorExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKxor, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return XOR_EXP;
	}

}
