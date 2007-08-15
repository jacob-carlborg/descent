package descent.internal.compiler.parser;

import java.util.List;

public class Pvirtuals
{
	public Expression e1;
	public List<Expression> exps;
	
	static int fpvirtuals(Object param, FuncDeclaration f)
	{
		Pvirtuals p = (Pvirtuals) param;

	    if(f.isVirtual())
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
