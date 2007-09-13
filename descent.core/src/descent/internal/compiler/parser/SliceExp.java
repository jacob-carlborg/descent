package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class SliceExp extends UnaExp {

	public Expression lwr;
	public Expression upr;
	public VarDeclaration lengthVar;

	public SliceExp(Loc loc, Expression e1, Expression lwr, Expression upr) {
		super(loc, TOK.TOKslice, e1);
		this.lwr = lwr;
		this.upr = upr;
		this.lengthVar = null;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, e1);
		}
		visitor.endVisit(this);
	}

	@Override
	public Expression syntaxCopy() {
		Expression lwr = null;
		if (this.lwr != null) {
			lwr = this.lwr.syntaxCopy();
		}

		Expression upr = null;
		if (this.upr != null) {
			upr = this.upr.syntaxCopy();
		}

		return new SliceExp(loc, e1.syntaxCopy(), lwr, upr);
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {	
		
		Expression e;
	    AggregateDeclaration ad;
	    ScopeDsymbol sym;
		
	    if(null != type)
	    	return this;
		
	    super.semantic(sc, context);
	    e1 = resolveProperties(sc, e1, context);
		
	    e = this;
	    Type t = e1.type.toBasetype(context);
	    
	    if (t.ty == TY.Tpointer)
	    {
			if (null == lwr || null == upr)
			    error("need upper and lower bound to slice pointer");
	    }
	    else if (t.ty == TY.Tarray || t.ty == TY.Tsarray)
	    {
	    }
	    else if(t.ty == TY.Tclass || t.ty == TY.Tstruct)
	    {
	    	if(t.ty == TY.Tclass)
	    		ad = ((TypeClass) t).sym;
	    	else
	    		ad = ((TypeStruct) t).sym;
	    	
	    	if (null != search_function(ad, Id.slice, context))
	        {
	    		// Rewrite as e1.slice(lwr, upr)
			    e = new DotIdExp(loc, e1, new IdentifierExp(Loc.ZERO,
			    		Id.slice));
			    if (null != lwr)
			    {
					assert(null != upr);
					e = new CallExp(loc, e, lwr, upr);
			    }
			    else
			    {
			    	assert(null == upr);
			    	e = new CallExp(loc, e);
			    }
			    
			    e = e.semantic(sc, context);
			    return e;
	        }
	    	else
	    	{
	    		return Lerror(t, e, sc, context);
	    	}
	    }
	    else if (t.ty == TY.Ttuple)
	    {
			if (null == lwr && null == upr)
			{
			    return e1;
			}
			else if (null == lwr || null == upr)
			{
				error("need upper and lower bound to slice tuple");
				return Lerror(t, e, sc, context);
			}
	    }
	    else
	    {
	    	return Lerror(t, e, sc, context);
	    }
	    
	    if (t.ty == TY.Tsarray || t.ty == TY.Tarray || t.ty == TY.Ttuple)
	    {
			sym = new ArrayScopeSymbol(this);
			sym.loc = loc;
			sym.parent = sc.scopesym;
			sc = sc.push(sym);
	    }
	    
	   
	    if (null != lwr)
	    {
	    	lwr = lwr.semantic(sc, context);
	    	lwr = resolveProperties(sc, lwr, context);
	    	lwr = lwr.implicitCastTo(sc, Type.tsize_t, context);
	    }
	    if (null != upr)
	    {
	    	upr = upr.semantic(sc, context);
	    	upr = resolveProperties(sc, upr, context);
	    	upr = upr.implicitCastTo(sc, Type.tsize_t, context);
	    }
	    
	    if (t.ty == TY.Tsarray || t.ty == TY.Tarray || t.ty == TY.Ttuple)
	    	sc.pop();
	    
	    if (t.ty == TY.Ttuple)
	    {
			lwr = lwr.optimize(WANTvalue, context);
			upr = upr.optimize(WANTvalue, context);
			int i1 = (int) lwr.toUInteger(context).longValue();
			int i2 = (int) upr.toUInteger(context).longValue();
		
			int length = 0;
			TupleExp te = null;
			TypeTuple tup = null;
		
			if (e1.op == TOK.TOKtuple)		// slicing an expression tuple
			{
				te = (TupleExp) e1;
			    length = te.exps.size();
			}
			else if (e1.op == TOK.TOKtype)	// slicing a type tuple
			{
				tup = (TypeTuple) t;
			    length = tup.arguments.size();
			}
			else
			{
			    assert(false);
			}
		
			if (i1 <= i2 && i2 <= length)
			{
				//int j1 = (size_t) i1;
			    //int j2 = (size_t) i2;
		
			    if (e1.op == TOK.TOKtuple)
			    {
			    	Expressions exps = new Expressions
			    			(i2 - i1);
					for (int i = 0; i < (i2 - i1); i++)
					{
						Expression tmp = (Expression) te.exps.get(i1 + i);
					    exps.set(i, tmp);
					}
					e = new TupleExp(loc, exps);
			    }
			    else
			    {
			    	Arguments args = new Arguments(i2 - i1);
					for (int i = i1; i < i2; i++)
					{
						Argument arg = tup.arguments.get(i);
					    args.add(arg);
					}
					e = new TypeExp(e1.loc, TypeTuple.newArguments(args));
				 }
				 e = e.semantic(sc, context);
			}
			else
			{
			    error("string slice [%ju .. %ju] is out of bounds", i1, i2);
			    e = e1;
			}
			return e;
	    }
	    else
	    {
		    type = t.next.arrayOf(context);
		    return e;
	    }
	}
	
	// Lerror:
	public Expression Lerror(Type t, Expression e,
			Scope sc, SemanticContext context)
	{
		String s;
	    if (t.ty == TY.Tvoid)
	    	s = e1.toChars(context);
	    else
	    	s = t.toChars(context);
	    
	    error("%s cannot be sliced with []", s);
	    type = Type.terror;
	    return e;
	}

	@Override
	public int getNodeType() {
		return SLICE_EXP;
	}

}
