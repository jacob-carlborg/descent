package descent.internal.compiler.parser;

public class ModExp extends BinExp {

	public ModExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKmod, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return MOD_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context)
	{
		Expression e;

	    if(null != type)
		return this;

	    super.semanticp(sc, context);
	    e = op_overload(sc);
	    if(null != e)
	    	return e;

	    typeCombine(sc, context);
	    e1.checkArithmetic(context);
	    e2.checkArithmetic(context);
	    if (type.isfloating())
	    {	type = e1.type;
			if (e2.type.iscomplex())
			{
				error("cannot perform modulo complex arithmetic");
				return new IntegerExp(Loc.ZERO, 0);
			}
	    }
	    return this;
	}
	
}
