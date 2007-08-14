package descent.internal.compiler.parser;


public class IdentityExp extends BinExp {

	public IdentityExp(Loc loc, TOK op, Expression e1, Expression e2) {
		super(loc, op, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return IDENTITY_EXP;
	}
	
	@Override
	public boolean isBit()
	{
		return true;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context)
	{
		if (null != type)
			return this;

		super.semanticp(sc, context);
		type = Type.tboolean;
		typeCombine(sc, context);
		if (e1.type != e2.type &&
				e1.type.isfloating() && e2.type.isfloating())
		{
			// Cast both to complex
			e1 = e1.castTo(sc, Type.tcomplex80, context);
			e2 = e2.castTo(sc, Type.tcomplex80, context);
		}
		return this;
	}

}
