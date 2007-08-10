package descent.internal.compiler.parser;


public class CmpExp extends BinExp {

	public CmpExp(Loc loc, TOK op, Expression e1, Expression e2) {
		super(loc, op, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return CMP_EXP;
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context)
	{
		Expression e;
	    Type t1;
	    Type t2;

		
	    if(null != type)
	    	return this;
	    
	    
	    super.semanticp(sc, context);
	    e = op_overload(sc);
	    if(null != e)
	    {
	    	e = new CmpExp(loc, op, e, new IntegerExp(loc, 0, Type.tint32));
	    	e = e.semantic(sc, context);
	    	return e;
	    }
	    
	    typeCombine(sc, context);
	    type = Type.tboolean;
	    
	    
	    // Special handling for array comparisons
	    t1 = e1.type.toBasetype(context);
	    t2 = e2.type.toBasetype(context);
	    if ((t1.ty == TY.Tarray || t1.ty == TY.Tsarray || t1.ty == TY.Tpointer) &&
	    	(t2.ty == TY.Tarray || t2.ty == TY.Tsarray || t2.ty == TY.Tpointer))
	    {
	    	/* NEXTOF if (t1->nextOf()->implicitConvTo(t2->nextOf()) < MATCHconst &&
	    		t2->nextOf()->implicitConvTo(t1->nextOf()) < MATCHconst &&
	    		(t1->nextOf()->ty != Tvoid && t2->nextOf()->ty != Tvoid))
	    		error("array comparison type mismatch, %s vs %s", 
	    				t1->nextOf()->toChars(), t2->nextOf()->toChars()); */
	    	e = this;
	    }
	    else if (t1.ty == TY.Tstruct || t2.ty == TY.Tstruct ||
	    		(t1.ty == TY.Tclass && t2.ty == TY.Tclass))
	    {
	    	if (t2.ty == TY.Tstruct)
	    		error("need member function opCmp() for %s %s to compare", 
	    				t2.toDsymbol(sc, context).kind(), t2.toChars());
	    	else
	    		error("need member function opCmp() for %s %s to compare", 
	    				t1.toDsymbol(sc, context).kind(), t1.toChars());
	    	e = this;
	    }
	    else if (t1.iscomplex() || t2.iscomplex())
	    {
	    	error("compare not defined for complex operands");
	    	e = new IntegerExp(loc, 0);
	    }
	    else
	    {
	    	e = this;
	    }
	    
	    return e;
	}

	@Override
	public boolean isBit()
	{
		return true;
	}
}
