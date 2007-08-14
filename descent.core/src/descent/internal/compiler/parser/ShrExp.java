package descent.internal.compiler.parser;

public class ShrExp extends BinExp {

	public ShrExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKshr, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return SHR_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context)
	{
		Expression e;

	    if(null == type)
	    {
	    	super.semanticp(sc, context);
			e = op_overload(sc);
			if(null != e)
			    return e;
			e1 = e1.checkIntegral(context);
			e2 = e2.checkIntegral(context);
			e1 = e1.integralPromotions(sc, context);
			e2 = e2.castTo(sc, Type.tshiftcnt, context);
			type = e1.type;
	    }
	    return this;
	}

}
