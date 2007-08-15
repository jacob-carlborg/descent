package descent.internal.compiler.parser;

import java.math.BigInteger;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class PostExp extends BinExp {

	public PostExp(Loc loc, TOK op, Expression e) {
		super(loc, op, e, new IntegerExp(Loc.ZERO, "1", BigInteger.ONE, Type.tint32));
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
}
