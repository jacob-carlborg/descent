package descent.internal.compiler.parser;

import java.math.BigInteger;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class PostExp extends BinExp {

	public PostExp(Loc loc, TOK op, Expression e) {
		super(loc, op, e, new IntegerExp(Loc.ZERO, 1, Type.tint32));
	}
	
	@Override
	public int getNodeType() {
		return POST_EXP;
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
		Expression e = this;
		
		if(null == type)
	    {
			super.semantic(sc, context);
			e2 = resolveProperties(sc, e2, context);

			e = op_overload(sc);
			if(null != e)
				return e;
	
			e = this;
			e1 = e1.modifiableLvalue(sc, null, context);
			e1.checkScalar(context);
			e1.checkNoBool(context);
			if (e1.type.ty == TY.Tpointer)
			    e = scaleFactor(sc, context);
			else
			    e2 = e2.castTo(sc, e1.type, context);
			e.type = e1.type;
	    }
	    return e;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context)
	{
		expToCBuffer(buf, hgs, e1, op.precedence, context);
	    buf.writestring((op == TOK.TOKplusplus) ? "++" : "--");
	}
	
}
