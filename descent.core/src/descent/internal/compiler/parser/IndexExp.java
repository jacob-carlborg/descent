package descent.internal.compiler.parser;

import java.math.BigInteger;

public class IndexExp extends BinExp {
	
	public VarDeclaration lengthVar;
	public int modifiable;

	public IndexExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKindex, e1, e2);
		// THey should implicitly get these values in Java, but this way
		// prettier.
		lengthVar = null;
		modifiable = 0;
	}

	@Override
	public int getNodeType() {
		return 0;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context)
	{
		
		Expression e;
	    BinExp b;
	    UnaExp u;
	    Type t1;
	    ScopeDsymbol sym;
		
	    if(null != type)
	    	return this;
	    
	    if(null == e1.type)
	    	e1 = e1.semantic(sc, context);
	    e = this;

	    t1 = e1.type.toBasetype(context);
	    
	    
	    if (t1.ty == TY.Tsarray || t1.ty == TY.Tarray || t1.ty == TY.Ttuple)
	    {
	    	// Create scope for 'length' variable
	    	sym = new ArrayScopeSymbol(this);
	    	sym.loc = loc;
	    	sym.parent = sc.scopesym;
	    	sc = sc.push(sym);
	    }
	    
	    
	    e2 = e2.semantic(sc, context);
	    if(null == e2.type)
	    {
	    	error("%s has no value", e2.toChars());
	    	e2.type = Type.terror;
	    }
	    e2 = resolveProperties(sc, e2, context);
	    
	    
	    if (t1.ty == TY.Tsarray || t1.ty == TY.Tarray || t1.ty == TY.Ttuple)
	    	sc = sc.pop();
	    
	    switch (t1.ty)
	    {
			case Tpointer:
			case Tarray:
			{
		    	e2 = e2.implicitCastTo(sc, Type.tsize_t, context);
		    	/* NEXTOF e.type = ((TypeNext) t1).next; */
		    	break;
		    }

			case Tsarray:
			{
		    	e2 = e2.implicitCastTo(sc, Type.tsize_t, context);

		    	TypeSArray tsa = (TypeSArray) t1;
		    	/* NEXTOF e.type = t1.nextOf(); */
		    	break;
			}

			case Taarray:
			{
				TypeAArray taa = (TypeAArray) t1;

		    	e2 = e2.implicitCastTo(sc, taa.index, context);	// type checking
		    	type = taa.next;
		    	break;
			}

			case Ttuple:
			{
		    	e2 = e2.implicitCastTo(sc, Type.tsize_t, context);
		   		e2 = e2.optimize(WANTvalue);
		    	BigInteger index = e2.toUInteger(context);
		    	BigInteger length = null;
		    	TupleExp te = null;
		    	TypeTuple tup = null;

		    	if (e1.op == TOK.TOKtuple)
		    	{
		    		te = (TupleExp) e1;
					length = BigInteger.valueOf(te.exps.size());
		    	}
		    	
		    	else if (e1.op == TOK.TOKtype)
		    	{
					tup = (TypeTuple) t1;
					length = BigInteger.valueOf(Argument.dim(tup.arguments, context));
		    	}
		    	
		    	else
					assert(false);

		    	if (index.longValue() < length.longValue())
		    	{
					if (e1.op == TOK.TOKtuple)
						e = te.exps.get((int) index.longValue());
					else
			    		e = new TypeExp(e1.loc, Argument.getNth(tup.arguments,
			    				(int) index.longValue(), context).type);
		    	}
		    	
		    	else
		    	{
					error("array index [%ju] is outside array bounds [0 .. %zu]", 
							(int) index.longValue(), (int) length.longValue());
					e = e1;
		    	}
		    	break;
			}

			default:
			{
		    	error("%s must be an array or pointer type, not %s",
				e1.toChars(), e1.type.toChars());
		    	type = Type.tint32;
		    	break;
		    }
		    
	    }
	    return e;
	}

	@Override
	public Expression modifiableLvalue(Scope sc, Expression e,
			SemanticContext context)
	{
		modifiable = 1;
	    if (e1.op == TOK.TOKstring)
	    	error("string literals are immutable");
	    if (null != type /* && NEXTOF !type.isMutable() */)
	    	error("%s is not mutable", e.toChars());
	    if (e1.type.toBasetype(context).ty == TY.Taarray)
	    	e1 = e1.modifiableLvalue(sc, e1, context);
	    return toLvalue(sc, e, context);
	}

	@Override
	public Expression toLvalue(Scope sc, Expression e, SemanticContext context)
	{
		return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context)
	{
		expToCBuffer(buf, hgs, e1, PREC.PREC_primary, context);
	    buf.writeByte('[');
	    expToCBuffer(buf, hgs, e2, PREC.PREC_assign, context);
	    buf.writeByte(']');
	}

}
