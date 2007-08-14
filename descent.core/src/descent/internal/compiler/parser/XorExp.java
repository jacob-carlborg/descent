package descent.internal.compiler.parser;

public class XorExp extends BinExp {

	public XorExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKxor, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return XOR_EXP;
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
			if (e1.type.toBasetype(context).ty == TY.Tbool &&
			    e2.type.toBasetype(context).ty == TY.Tbool)
			{
			    type = e1.type;
			    e = this;
			}
			else
			{
			    typeCombine(sc, context);
			    e1.checkIntegral(context);
			    e2.checkIntegral(context);
			}
	    }
	    return this;
	}

}
