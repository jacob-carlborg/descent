package descent.internal.compiler.parser;

public class ForeachRangeStatement extends Statement {

	public TOK op;
	public Argument arg;
	public Expression lwr;
	public Expression upr;
	public Statement body;

	public ForeachRangeStatement(Loc loc, TOK op, Argument arg, Expression lwr, Expression upr, Statement body) {
		super(loc);
		
		this.op = op;
		this.arg = arg;
		this.lwr = lwr;
		this.upr = upr;
		this.body = body;
	}

	@Override
	public int getNodeType() {
		return FOREACH_RANGE_STATEMENT;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context)
	{
	    ScopeDsymbol sym;
	    Statement s = this;

	    lwr = lwr.semantic(sc, context);
	    lwr = resolveProperties(sc, lwr, context);
	    if(null == lwr.type)
	    {
			error("invalid range lower bound %s", lwr.toChars());
			return this;
	    }

	    upr = upr.semantic(sc, context);
	    upr = resolveProperties(sc, upr, context);
	    if(null == upr.type)
	    {
			error("invalid range upper bound %s", upr.toChars());
			return this;
	    }

	    if(null != arg.type)
	    {
			lwr = lwr.implicitCastTo(sc, arg.type, context);
			upr = upr.implicitCastTo(sc, arg.type, context);
	    }
	    else
	    {
			/* Must infer types from lwr and upr
			 */
			AddExp ea = new AddExp(loc, lwr, upr);
			ea.typeCombine(sc, context);
			arg.type = ea.type;
			lwr = ea.e1;
			upr = ea.e2;
	    }
	    
	    if (!arg.type.isscalar())
	    	error("%s is not a scalar type", arg.type.toChars());

	    sym = new ScopeDsymbol(Loc.ZERO);
	    sym.parent = sc.scopesym;
	    sc = sc.push(sym);

	    sc.noctor++;

	    /* WTF did key come from?
	     * key = new VarDeclaration(loc, arg.type, arg.ident, null);
	     */
	    VarDeclaration key = new VarDeclaration(loc, arg.type, arg.ident, null);
	    DeclarationExp de = new DeclarationExp(loc, key);
	    de.semantic(sc, context);

	    if(0 < key.storage_class)
	    	error("foreach range: key cannot have storage class");

	    sc.sbreak = this;
	    sc.scontinue = this;
	    body = body.semantic(sc, context);

	    sc.noctor--;
	    sc.pop();
	    return s;
	}

	@Override
	public Statement syntaxCopy()
	{
		ForeachRangeStatement s = new ForeachRangeStatement(loc, op,
				arg.syntaxCopy(),
				lwr.syntaxCopy(),
				upr.syntaxCopy(),
				null != body ? body.syntaxCopy() : null);
		return s;
	}

	@Override
	public boolean fallOffEnd()
	{
		if (null != body)
			body.fallOffEnd();
		return true;
	}

}
