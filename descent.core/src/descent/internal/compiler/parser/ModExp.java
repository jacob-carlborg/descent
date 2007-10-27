package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.Constfold.Mod;

// DMD 1.020
public class ModExp extends BinExp {

	public ModExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKmod, e1, e2);
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
		return MOD_EXP;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		return interpretCommon(istate, Mod, context);
	}

	@Override
	public char[] opId() {
		return Id.mod;
	}

	@Override
	public char[] opId_r() {
		return Id.mod_r;
	}

	@Override
	public Expression optimize(int result, SemanticContext context) {
		Expression e;

		e1 = e1.optimize(result, context);
		e2 = e2.optimize(result, context);
		if (e1.isConst() && e2.isConst()) {
			e = Mod.call(type, e1, e2, context);
		} else {
			e = this;
		}
		return e;
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

		typeCombine(sc, context);
		e1.checkArithmetic(context);
		e2.checkArithmetic(context);
		if (type.isfloating()) {
			type = e1.type;
			if (e2.type.iscomplex()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.CannotPerformModuloComplexArithmetic, this));
				return new IntegerExp(Loc.ZERO, 0);
			}
		}
		
		return this;
	}

}
