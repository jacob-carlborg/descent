package descent.internal.compiler.parser;

import java.math.BigInteger;

public class PostExp extends BinExp {

	public PostExp(Loc loc, TOK op, Expression e) {
		super(loc, op, e, new IntegerExp(Loc.ZERO, "1", BigInteger.ONE, Type.tint32));
	}
	
	@Override
	public int getNodeType() {
		return POST_EXP;
	}

}
