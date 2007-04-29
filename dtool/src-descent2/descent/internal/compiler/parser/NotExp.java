package descent.internal.compiler.parser;

public class NotExp extends UnaExp {

	public NotExp(Loc loc, Expression e1) {
		super(loc, TOK.TOKnot, e1);
	}
	
	@Override
	public int getNodeType() {
		return NOT_EXP;
	}
	
	@Override
	public boolean isBit() {
		return true;
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		super.semantic(sc, context);
	    e1 = resolveProperties(sc, e1, context);
	    e1 = e1.checkToBoolean(context);
	    type = Type.tboolean;
	    return this;
	}

}
