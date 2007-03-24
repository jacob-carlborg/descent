package descent.internal.compiler.parser;

public class HaltExp extends Expression {

	public HaltExp() {
		super(TOK.TOKhalt);
	}

	@Override
	public int getNodeType() {
		return HALT_EXP;
	}

}
