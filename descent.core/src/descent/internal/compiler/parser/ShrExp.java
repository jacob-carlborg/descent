package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class ShrExp extends BinExp {

	public ShrExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKshr, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return SHR_EXP;
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

	    if(null == type)
	    {
	    	super.semanticp(sc, context);
			e = op_overload(sc);
			if(null != e)
			    return e;
			e1 = e1.checkIntegral(context);
			e2 = e2.checkIntegral(context);
			e1 = e1.integralPromotions(sc, context);
			e2 = e2.castTo(sc, Type.tshiftcnt, context);
			type = e1.type;
	    }
	    return this;
	}
	
}
