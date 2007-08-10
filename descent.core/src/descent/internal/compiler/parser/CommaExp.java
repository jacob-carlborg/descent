package descent.internal.compiler.parser;

public class CommaExp extends BinExp {

	public CommaExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKcomma, e1, e2);
	}
	
	@Override
	public boolean isBool(boolean result) {
		return e2.isBool(result);
	}
	
	@Override
	public int getNodeType() {
		return COMMA_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context)
	{
		if(type == null)
	    {
			super.semanticp(sc, context);
			type = e2.type;
	    }
	    return this;
	}

	@Override
	public void checkEscape(SemanticContext context)
	{
		e2.checkEscape(context);
	}

	@Override
	public int checkSideEffect(int flag, SemanticContext context)
	{
		if (flag == 2)
		{
			int result = e1.checkSideEffect(2, context);
			return result > 0 ? result : e2.checkSideEffect(2, context);
		}
		else
		{
			// Don't check e1 until we cast(void) the a,b code generation
		    return e2.checkSideEffect(flag, context);
		}
	}

	@Override
	public Expression modifiableLvalue(Scope sc, Expression e,
			SemanticContext context)
	{
		 e2 = e2.modifiableLvalue(sc, e, context);
		 return this;
	}

	@Override
	public Expression toLvalue(Scope sc, Expression e, SemanticContext context)
	{
		e2 = e2.toLvalue(sc, null, context);
	    return this;
	}
	
	
}
