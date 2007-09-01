package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class ModExp extends BinExp {

	public ModExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKmod, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return MOD_EXP;
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
	    e = op_overload(sc, context);
	    if(null != e)
	    	return e;

	    typeCombine(sc, context);
	    e1.checkArithmetic(context);
	    e2.checkArithmetic(context);
	    if (type.isfloating())
	    {	type = e1.type;
			if (e2.type.iscomplex())
			{
				error("cannot perform modulo complex arithmetic");
				return new IntegerExp(Loc.ZERO, 0);
			}
	    }
	    return this;
	}
	
}
