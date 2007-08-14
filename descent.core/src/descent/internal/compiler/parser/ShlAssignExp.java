package descent.internal.compiler.parser;

public class ShlAssignExp extends BinExp {

	public ShlAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKshlass, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return SHL_ASSIGN_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context)
	{
		Expression e;

	    super.semantic(sc, context);
	    e2 = resolveProperties(sc, e2, context);

	    e = op_overload(sc);
	    if(null != e)
	    	return e;

	    e1 = e1.modifiableLvalue(sc, null, context);
	    e1.checkScalar(context);
	    e1.checkNoBool(context);
	    type = e1.type;
	    typeCombine(sc, context);
	    e1.checkIntegral(context);
	    e2 = e2.checkIntegral(context);
	    e2 = e2.castTo(sc, Type.tshiftcnt, context);
	    return this;
	}
}
