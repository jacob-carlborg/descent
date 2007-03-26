package descent.internal.compiler.parser;

public class StringExp extends Expression {
	
	public String string;
	public char postfix;
	public char sz;	// 1: char, 2: wchar, 4: dchar
    public char committed;	// !=0 if type is committed
	
	public StringExp(String string) {
		this(string, (char) 0);
	}
	
	public StringExp(String string, char postfix) {
		super(TOK.TOKstring);
		this.string = string;
		this.postfix = postfix;
	}
	
	@Override
	public int getNodeType() {
		return STRING_EXP;
	}
	
	@Override
	public boolean isBool(boolean result) {
		return result;
	}

	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		// TODO semantic
		return this;
	}

	public StringExp toUTF8(Scope sc) {
		// TODO semantic
		return this;
	}

}
