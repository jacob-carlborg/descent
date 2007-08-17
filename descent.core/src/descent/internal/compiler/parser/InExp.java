package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class InExp extends BinExp {

	public InExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKin, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return IN_EXP;
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

	    if(null != type)
		return this;

	    super.semanticp(sc, context);
	    e = op_overload(sc);
	    if(null != e)
	    	return e;

	    Type t2b = e2.type.toBasetype(context);
	    if (t2b.ty != TY.Taarray)
	    {
	    	error("rvalue of in expression must be an associative array, not %s", e2.type.toChars());
	    	type = Type.terror;
	    }
	    else
	    {
	    	TypeAArray ta = (TypeAArray) t2b;

	    	// Convert key to type of key
	    	e1 = e1.implicitCastTo(sc, ta.index, context);

	    	// Return type is pointer to value
	    	/* NEXTOF type = ta.nextOf().pointerTo(); */
	    }
	    return this;
	}

	@Override
	public boolean isBit()
	{
		return false;
	}
	
}
