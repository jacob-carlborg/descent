package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.Constfold.Add;
import static descent.internal.compiler.parser.Constfold.Min;
import static descent.internal.compiler.parser.TOK.TOKplusplus;

// DMD 1.020
public class PostExp extends BinExp {
	
	public PostExp(Loc loc, TOK op, Expression e) {
		super(loc, op, e, new IntegerExp(Loc.ZERO, 1, Type.tint32));
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceE1);
			TreeVisitor.acceptChildren(visitor, sourceE2);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return POST_EXP;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		Expression e;
		if (op == TOKplusplus) {
			e = interpretAssignCommon(istate, Add, 1, context);
		} else {
			e = interpretAssignCommon(istate, Min, 1, context);
		}
		return e;
	}

	@Override
	public char[] opId() {
		return (op == TOKplusplus) ? Id.postinc : Id.postdec;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression e = this;

		if (null == type) {
			super.semantic(sc, context);
			e2 = resolveProperties(sc, e2, context);

			e = op_overload(sc, context);
			if (null != e) {
				return e;
			}

			e = this;
			e1 = e1.modifiableLvalue(sc, null, context);
			e1.checkScalar(context);
			e1.checkNoBool(context);
			if (e1.type.ty == TY.Tpointer) {
				e = scaleFactor(sc, context);
			} else {
				e2 = e2.castTo(sc, e1.type, context);
			}
			e.type = e1.type;
		}
		
		return e;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		expToCBuffer(buf, hgs, e1, op.precedence, context);
		buf.writestring((op == TOK.TOKplusplus) ? "++" : "--");
	}

}
