package descent.internal.compiler.parser;

import java.math.BigInteger;

import descent.internal.compiler.parser.ast.IASTVisitor;

public class VoidInitializer extends Initializer {
	
	public Type type;
	
	public VoidInitializer(Loc loc) {
		super(loc);
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		visitor.endVisit(this);
	}
	
	
	@Override
	public Expression toExpression(SemanticContext context) {
		error("void initializer has no value");
	    return new IntegerExp(loc, "0", BigInteger.ZERO, Type.tint32);
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
