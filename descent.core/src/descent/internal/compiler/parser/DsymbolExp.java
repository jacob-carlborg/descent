package descent.internal.compiler.parser;

public class DsymbolExp extends Expression {
	
	public Dsymbol s;

	public DsymbolExp(Dsymbol s) {
		super(TOK.TOKdsymbol);
		this.s = s;
	}

	@Override
	public int kind() {
		return DSYMBOL_EXP;
	}

}
