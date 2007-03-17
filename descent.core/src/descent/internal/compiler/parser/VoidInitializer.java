package descent.internal.compiler.parser;

import java.math.BigInteger;

public class VoidInitializer extends Initializer {
	
	@Override
	public Expression toExpression() {
		/* TODO semantic
		error(loc, "void initializer has no value");
		*/
	    return new IntegerExp("0", BigInteger.ZERO, Type.tint32);
	}
	
	@Override
	public VoidInitializer isVoidInitializer() {
		return this;
	}
	
	@Override
	public int kind() {
		return VOID_INITIALIZER;
	}

}
