package descent.internal.compiler.parser;

public class StringExp extends Expression {
	
	public String string;
	
	public StringExp(String string) {
		super(TOK.TOKstring);
		this.string = string;
	}
	
	@Override
	public int kind() {
		return STRING_EXP;
	}

}
