package descent.internal.compiler.parser;

import java.math.BigInteger;

public class VoidInitializer extends Initializer {
	
	public Type type;
	
	@Override
	public Expression toExpression(SemanticContext context) {
		error("void initializer has no value");
	    return new IntegerExp("0", BigInteger.ZERO, Type.tint32);
	}
	
	@Override
	public VoidInitializer isVoidInitializer() {
		return this;
	}
	
	@Override
	public Initializer semantic(Scope sc, Type t, SemanticContext context) {
		type = t;
	    return this;
	}
	
	@Override
	public int getNodeType() {
		return VOID_INITIALIZER;
	}

}
