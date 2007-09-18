package descent.internal.compiler.parser;

// DMD 1.020
public class Pvirtuals
{
	public Expression e1;
	public Expressions exps;
	
	static int fpvirtuals(Object param, FuncDeclaration f, SemanticContext context)
	{
		Pvirtuals p = (Pvirtuals) param;

	    if(f.isVirtual(context))
	    {
	    	Expression e;

			if (p.e1.op == TOK.TOKdotvar)
			{
				DotVarExp dve = (DotVarExp) p.e1;
			    e = new DotVarExp(Loc.ZERO, dve.e1, f);
			}
			else
			    e = new DsymbolExp(Loc.ZERO, f);
			p.exps.add(e);
	    }
	    
	    return 0;
	}
}
