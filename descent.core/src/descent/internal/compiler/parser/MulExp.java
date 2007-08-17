package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class MulExp extends BinExp {

	public MulExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKmul, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return MUL_EXP;
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
	    {
	    	return this;
	    }

	    super.semanticp(sc, context);
	    e = op_overload(sc);
	    if (null != e)
	    	return e;

	    typeCombine(sc, context);
	    e1.checkArithmetic(context);
	    e2.checkArithmetic(context);
	    if (type.isfloating())
	    {
	    	Type t1 = e1.type;
			Type t2 = e2.type;
	
			if (t1.isreal())
			{
			    type = t2;
			}
			else if (t2.isreal())
			{
			    type = t1;
			}
			else if (t1.isimaginary())
			{
			    if (t2.isimaginary())
			    {
	
					switch(t1.ty)
					{
					    case Timaginary32:	type = Type.tfloat32;	break;
					    case Timaginary64:	type = Type.tfloat64;	break;
					    case Timaginary80:	type = Type.tfloat80;	break;
					    default:		    assert(false);
					}
		
					// iy * iv = -yv
					e1.type = type;
					e2.type = type;
					e = new NegExp(loc, this);
					e = e.semantic(sc, context);
					return e;
			    }
			    else
			    {
			    	type = t2;	// t2 is complex
			    }
			}
			else if (t2.isimaginary())
			{
			    type = t1;	// t1 is complex
			}
	    }
	    return this;
	}

	
}
