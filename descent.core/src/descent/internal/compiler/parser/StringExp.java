package descent.internal.compiler.parser;

public class StringExp extends Expression {
	
	public String string;
	public char postfix;
	
	public StringExp(String string) {
		this(string, (char) 0);
	}
	
	public StringExp(String string, char postfix) {
		super(TOK.TOKstring);
		this.string = string;
		this.postfix = postfix;
	}
	
	@Override
	public int kind() {
		return STRING_EXP;
	}

}
