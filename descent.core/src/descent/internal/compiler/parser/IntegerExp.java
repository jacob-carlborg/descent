package descent.internal.compiler.parser;

import java.math.BigInteger;

public class IntegerExp extends Expression {

	public String str;
	public BigInteger value;
	
	public IntegerExp(String str, BigInteger value, Type type) {
		super(TOK.TOKint64);
		this.str = str;
		this.value = value;
		this.type = type;
	}
	
	@Override
	public int kind() {
		return INTEGER_EXP;
	}

}
