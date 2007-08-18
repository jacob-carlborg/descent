package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;


public class AssignExp extends BinExp {

	public AssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKassign, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return ASSIGN_EXP;
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
	public Expression checkToBoolean(SemanticContext context)
	{
		error("'=' does not give a boolean result");
	    return this;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context)
	{   
	    /* Look for operator overloading of a[i]=value.
	     * Do it before semantic() otherwise the a[i] will have been
	     * converted to a.opIndex() already.
	     */
	    if (e1.op == TOK.TOKarray)
	    {	
	    	Type t1;
			ArrayExp ae = (ArrayExp) e1;
			AggregateDeclaration ad;
			char[] id = Id.index;
	
			ae.e1 = ae.e1.semantic(sc, context);
			t1 = ae.e1.type.toBasetype(context);
			
			if(t1.ty == TY.Tstruct || t1.ty == TY.Tclass)
			{
				if (t1.ty == TY.Tstruct)
				{
				    ad = ((TypeStruct) t1).sym;
				}
				else // t1.ty == TY.Tclass
				{
					ad = ((TypeClass) t1).sym;
				}
				
			    // Rewrite (a[i] = value) to (a.opIndexAssign(value, i))
			    if(null != search_function(ad, Id.indexass, context))
			    {
			    	Expression e = new DotIdExp(loc, ae.e1, 
			    			new IdentifierExp(Loc.ZERO, Id.indexass));
			    	List<Expression> a = 
			    		new ArrayList<Expression>(ae.arguments);
	
					a.add(e2); // WTF a.add(0, e2); -- Add at position 0 or add a null and then e2?
					e = new CallExp(loc, e, a);
					e = e.semantic(sc, context);
					return e;
			    }
			    else
			    {
					// Rewrite (a[i] = value) to (a.opIndex(i, value))
					if(null != search_function(ad, id, context))
					{
						Expression e = new DotIdExp(loc, ae.e1, 
								new IdentifierExp(Loc.ZERO, id));
					
						error("operator [] assignment overload with opIndex(i, value) illegal, use opIndexAssign(value, i)");
		
					    e = new CallExp(loc, e, (Expression) ae.arguments.get(0), e2);
					    e = e.semantic(sc, context);
					    return e;
					}
			    }
			}
	    }
	    
	    /* Look for operator overloading of a[i..j]=value.
	     * Do it before semantic() otherwise the a[i..j] will have been
	     * converted to a.opSlice() already.
	     */
	    if (e1.op == TOK.TOKslice)
	    {
	    	Type t1;
			SliceExp ae = (SliceExp) e1;
			AggregateDeclaration ad;
			char[] id = Id.index;
	
			ae.e1 = ae.e1.semantic(sc, context);
			ae.e1 = resolveProperties(sc, ae.e1, context);
			t1 = ae.e1.type.toBasetype(context);
			
			if(t1.ty == TY.Tstruct || t1.ty == TY.Tclass)
			{
				if (t1.ty == TY.Tstruct)
				{
				    ad = ((TypeStruct) t1).sym;
				    
				}
				else // t1.ty == TY.Tclass
				{
				    ad = ((TypeClass) t1).sym;
				}
				
			    // Rewrite (a[i..j] = value) to (a.opIndexAssign(value, i, j))
			    if(null != search_function(ad, Id.sliceass, context))
			    {
			    	Expression e = new DotIdExp(loc, ae.e1, 
			    			new IdentifierExp(Loc.ZERO, Id.sliceass));
			    	List<Expression> a = new ArrayList<Expression>();
	
					a.add(e2);
					if(null != ae.lwr)
					{
						a.add(ae.lwr);
					    assert(null != ae.upr);
					    a.add(ae.upr);
					}
					else
					    assert(null == ae.upr);
					
					e = new CallExp(loc, e, a);
					e = e.semantic(sc, context);
					return e;
			    }
			}
	    }
	    
	    Expression e1old = e1;
	    Type t1;
	    
	    super.semantic(sc, context);
	    e2 = resolveProperties(sc, e2, context);
	    assert(null != e1.type);

	    t1 = e1.type.toBasetype(context);

	    if (t1.ty == TY.Tfunction)
	    {
	    	// Rewrite f=value to f(value)
			Expression e;
	
			e = new CallExp(loc, e1, e2);
			e = e.semantic(sc, context);
			return e;
	    }

	    /* If it is an assignment from a 'foreign' type,
	     * check for operator overloading.
	     */
	    if (t1.ty == TY.Tclass || t1.ty == TY.Tstruct)
	    {
			if (MATCH.MATCHnomatch == 
				e2.type.implicitConvTo(e1.type, context))
			{
			    Expression e = op_overload(sc);
			    if(null != e)
			    	return e;
			}
	    }

	    e2.rvalue(context);

	    /* TODO semantic:
	    if (e1.op == TOK.TOKarraylength)
	    {
			// e1 is not an lvalue, but we let code generator handle it
			ArrayLengthExp ale = (ArrayLengthExp) e1;
			ale.e1 = ale.e1.modifiableLvalue(sc, null);
	    }
	    else */
	    
	    /* NEXTOF
	    if (e1.op == TOK.TOKslice)
	    {
		    Type tn = e1.type.nextOf();
			if (tn && !tn.isMutable() && op != TOKconstruct)
			    error("slice %s is not mutable", e1.toChars());
	    }
	    
	    
	    else */
	    {
	    	// Try to do a decent error message with the expression
	    	// before it got constant folded
	    	e1 = e1.optimize(WANTvalue);
	    	e1 = e1.modifiableLvalue(sc, e1old, context);
	    }

	    Type t2 = e2.type;
	    if (e1.op == TOK.TOKslice &&
	    		/* NEXTOF t1.nextOf() && */
	    		MATCH.MATCHnomatch != e2.implicitConvTo(t1/* NEXTOF .nextOf() */, context)
	       )
	    {	// memset
			/* PERHAPS ismemset = 1; */	// make it easy for back end to tell what this is
			e2 = e2.implicitCastTo(sc, t1/* NEXTOF .nextOf() */, context);
	    }
	    else if (t1.ty == TY.Tsarray)
	    {
	    	error("cannot assign to static array %s", e1.toChars());
	    }
	    else if (e1.op == TOK.TOKslice)
	    {
	    	e2 = e2.implicitCastTo(sc, e1.type/* NEXTOF .constOf() */, context);
	    }
	    else
	    {
	    	e2 = e2.implicitCastTo(sc, e1.type, context);
	    }
	    
	    type = e1.type;
	    assert(null != type);
	    return this;
	}
}
