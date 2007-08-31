package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;


public class EqualExp extends BinExp {

	public EqualExp(Loc loc, TOK op, Expression e1, Expression e2) {
		super(loc, op, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return EQUAL_EXP;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, e1);
			TreeVisitor.acceptChildren(visitor, e2);
		}
		visitor.endVisit(this);
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context)
	{
		Expression e;
	    Type t1;
	    Type t2;
	    
	    if (null != type)
		return this;

	    super.semanticp(sc, context);

	    /* Before checking for operator overloading, check to see if we're
	     * comparing the addresses of two statics. If so, we can just see
	     * if they are the same symbol.
	     */
	    if(e1.op == TOK.TOKaddress && e2.op == TOK.TOKaddress)
	    {
	    	AddrExp ae1 = (AddrExp) e1;
	    	AddrExp ae2 = (AddrExp) e2;
		
	    	if (ae1.e1.op == TOK.TOKvar && ae2.e1.op == TOK.TOKvar)
	    	{
	    		VarExp ve1 = (VarExp) ae1.e1;
	    		VarExp ve2 = (VarExp) ae2.e1;

	    		if(ve1.var == ve2.var)
	    		{
	    			// They are the same, result is 'true' for ==, 'false' for !=
	    			e = new IntegerExp(loc, (op == TOK.TOKequal) ? 1 : 0,
	    					Type.tboolean);
	    			return e;
	    		}
	    	}
	    }
	    
		
		e = op_overload(sc);
		if(null != e)
		{
		    if(op == TOK.TOKnotequal)
		    {
		    	e = new NotExp(e.loc, e);
		    	e = e.semantic(sc, context);
		    }
		    return e;
		}
	    
	    e = typeCombine(sc, context);
	    type = Type.tboolean;
	    
	    // Special handling for array comparisons
	    t1 = e1.type.toBasetype(context);
	    t2 = e2.type.toBasetype(context);
	    if ((t1.ty == TY.Tarray || t1.ty == TY.Tsarray) &&
	    	(t2.ty == TY.Tarray || t2.ty == TY.Tsarray))
	    {
	    	if (!t1.next.equals(t2.next))
	    		error("array comparison type mismatch, %s vs %s", t1.next.toChars(context), t2.next.toChars(context));
	    }
	    
	    else
	    {
	    	if (e1.type != e2.type && e1.type.isfloating()
	    			&& e2.type.isfloating())
	    	{
	    		// Cast both to complex
	    		e1 = e1.castTo(sc, Type.tcomplex80, context);
	    		e2 = e2.castTo(sc, Type.tcomplex80, context);
	    	}
	    }
	    return e;
	}

	@Override
	public boolean isBit()
	{
		return true;
	}
}
