package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class IdentityExp extends BinExp {

	public IdentityExp(Loc loc, TOK op, Expression e1, Expression e2) {
		super(loc, op, e1, e2);
	}

	@Override
	public int getNodeType() {
		return IDENTITY_EXP;
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
	public boolean isBit() {
		return true;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (null != type)
			return this;

		super.semanticp(sc, context);
		type = Type.tboolean;
		typeCombine(sc, context);
		if (e1.type != e2.type && e1.type.isfloating() && e2.type.isfloating()) {
			// Cast both to complex
			e1 = e1.castTo(sc, Type.tcomplex80, context);
			e2 = e2.castTo(sc, Type.tcomplex80, context);
		}
		return this;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		return interpretCommon2(istate, op, context);
	}

}
