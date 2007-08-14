package descent.internal.compiler.parser;

public class ShlExp extends BinExp {

	public ShlExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKshl, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return SHL_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context)
	{
	    if(null == type)
	    {
	    	Expression e;
	    	
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
