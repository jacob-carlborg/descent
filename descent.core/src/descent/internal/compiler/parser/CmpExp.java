package descent.internal.compiler.parser;


public class CmpExp extends BinExp {

	public CmpExp(TOK op, Expression e1, Expression e2) {
		super(op, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return CMP_EXP;
	}

}
