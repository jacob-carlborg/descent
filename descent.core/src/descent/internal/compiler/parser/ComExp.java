package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class ComExp extends UnaExp {

	public ComExp(Loc loc, Expression e1) {
		super(loc, TOK.TOKtilde, e1);
	}

	@Override
	public int getNodeType() {
		return COM_EXP;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, e1);
		}
		visitor.endVisit(this);
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression e;

		if (type == null) {
			super.semantic(sc, context);
			e1 = resolveProperties(sc, e1, context);
			e = op_overload(sc, context);
			if (e != null) {
				return e;
			}

			e1.checkNoBool(context);
			e1 = e1.checkIntegral(context);
			type = e1.type;
		}
		return this;
	}
	
	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		return interpretCommon(istate, op, context);
	}

}
