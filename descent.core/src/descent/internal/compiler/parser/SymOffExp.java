package descent.internal.compiler.parser;

public class SymOffExp extends Expression {

	public Declaration var;
	public int offset;

	public SymOffExp(Declaration var, int offset) {
		super(TOK.TOKsymoff);
		this.var = var;
		this.offset = offset;
	}

	@Override
	public int getNodeType() {
		return SYM_OFF_EXP;
	}

}
