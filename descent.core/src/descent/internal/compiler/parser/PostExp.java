package descent.internal.compiler.parser;

import java.math.BigInteger;

public class PostExp extends BinExp {

	public PostExp(TOK op, Expression e) {
		super(op, e, new IntegerExp("1", BigInteger.ONE, Type.tint32));
	}
	
	@Override
	public int kind() {
		return POST_EXP;
	}

}
