package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class InExp extends BinExp {

	public InExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKin, e1, e2);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, e1);
			TreeVisitor.acceptChildren(visitor, e2);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return IN_EXP;
	}

	@Override
	public boolean isBit() {
		return false;
	}

	@Override
	public char[] opId() {
		return Id.opIn;
	}

	@Override
	public char[] opId_r() {
		return Id.opIn_r;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression e;

		if (null != type) {
			return this;
		}

		super.semanticp(sc, context);
		e = op_overload(sc, context);
		if (null != e) {
			return e;
		}

		Type t2b = e2.type.toBasetype(context);
		if (t2b.ty != TY.Taarray) {
			error(
					"rvalue of in expression must be an associative array, not %s",
					e2.type.toChars(context));
			type = Type.terror;
		} else {
			TypeAArray ta = (TypeAArray) t2b;

			// Convert key to type of key
			e1 = e1.implicitCastTo(sc, ta.index, context);

			// Return type is pointer to value
			type = ta.next.pointerTo(context);
		}
		return this;
	}

}