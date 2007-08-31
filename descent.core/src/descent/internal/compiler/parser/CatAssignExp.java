package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class CatAssignExp extends BinExp {

	public CatAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKcatass, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return CAT_ASSIGN_EXP;
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

	    super.semantic(sc, context);
	    e2 = resolveProperties(sc, e2, context);

	    e = op_overload(sc);
	    if (null != e)
	    	return e;
	    
	    if (e1.op == TOK.TOKslice)
	    {
	    	SliceExp se = (SliceExp) e1;

	    	if (se.e1.type.toBasetype(context).ty == TY.Tsarray)
	    		error("cannot append to static array %s", se.e1.type.toChars(context));
	    }

	    e1 = e1.modifiableLvalue(sc, null, context);
	    

	    Type tb1 = e1.type.toBasetype(context);
	    Type tb2 = e2.type.toBasetype(context);

	    if ((tb1.ty == TY.Tarray) &&
	    	(tb2.ty == TY.Tarray || tb2.ty == TY.Tsarray) &&
	    	MATCH.MATCHnomatch != (e2.implicitConvTo(e1.type, context)))
	    {
	    	// Append array
	    	e2 = e2.castTo(sc, e1.type, context);
	    	type = e1.type;
	    	e = this;
	    }
	    
	    else if ((tb1.ty == TY.Tarray) 
	    		&& null != e2.implicitConvTo(tb1.next, context))
	    {
	    	// Append element
	    	e2 = e2.castTo(sc, tb1.next, context);
	    	type = e1.type;
	    	e = this;
	    }
	    
	    else
	    {
			error("cannot append type %s to type %s", tb2.toChars(context), tb1.toChars(context));
			type = Type.tint32;
			e = this;
	    }
	    
	    return e;
	}
	
	
	
}
