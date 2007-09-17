package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.TY.*;

// DMD 1.020
public class AndAndExp extends BinExp {

	public AndAndExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKandand, e1, e2);
	}

	@Override
	public int getNodeType() {
		return AND_AND_EXP;
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
	public Expression semantic(Scope sc, SemanticContext context) {
		e1 = e1.semantic(sc, context);
		e1 = resolveProperties(sc, e1, context);
		e1 = e1.checkToPointer(context);
		e1 = e1.checkToBoolean(context);
		int cs1 = sc.callSuper;

		if ((sc.flags & Scope.SCOPEstaticif) > 0) {
			//If in static if, don't evaluate e2 if we don't have to.
			e1 = e1.optimize(WANTflags, context);
			if (e1.isBool(false))
				return new IntegerExp(loc, 0, Type.tboolean);
		}

		e2 = e2.semantic(sc, context);
		sc.mergeCallSuper(loc, cs1);
		e2 = resolveProperties(sc, e2, context);
		e2 = e2.checkToPointer(context);

		type = Type.tboolean;
		if (e1.type.ty == TY.Tvoid)
			type = Type.tvoid;
		if (e2.op == TOK.TOKtype || e2.op == TOK.TOKimport) {
			error(e2.toChars(context) + " is not an expression.");
		}

		return this;
	}

	@Override
	public Expression checkToBoolean(SemanticContext context) {
		e2 = e2.checkToBoolean(context);
		return this;
	}

	@Override
	public boolean isBit() {
		return true;
	}

	@Override
	public int checkSideEffect(int flag, SemanticContext context) {
		if (flag == 2) {
			int temp = e1.checkSideEffect(2, context);
			return temp != 0 ? temp : e2.checkSideEffect(2, context);
		} else {
			e1.checkSideEffect(1, context);
			return e2.checkSideEffect(flag, context);
		}
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		Expression e = e1.interpret(istate, context);
		if (e != EXP_CANT_INTERPRET) {
			if (e.isBool(false))
				e = new IntegerExp(e1.loc, 0, type);
			else if (e.isBool(true)) {
				e = e2.interpret(istate, context);
				if (e != EXP_CANT_INTERPRET) {
					if (e.isBool(false))
						e = new IntegerExp(e1.loc, 0, type);
					else if (e.isBool(true))
						e = new IntegerExp(e1.loc, 1, type);
					else
						e = EXP_CANT_INTERPRET;
				}
			} else
				e = EXP_CANT_INTERPRET;
		}
		return e;
	}

	@Override
	public Expression optimize(int result, SemanticContext context) {
		Expression e;

		e1 = e1.optimize(WANTflags | (result & WANTinterpret), context);
		e = this;
		if (e1.isBool(false)) {
			e = new CommaExp(loc, e1, new IntegerExp(loc, 0, type));
			e.type = type;
			e = e.optimize(result, context);
		} else {
			e2 = e2.optimize(WANTflags | (result & WANTinterpret), context);
			if (result > 0 && e2.type.toBasetype(context).ty == Tvoid
					&& context.global.errors <= 0)
				error("void has no value");
			if (e1.isConst()) {
				if (e2.isConst()) {
					boolean n1 = e1.isBool(true);
					boolean n2 = e2.isBool(true);

					e = new IntegerExp(loc, n1 && n2 ? 1 : 0, type);
				} else if (e1.isBool(true))
					e = new BoolExp(loc, e2, type);
			}
		}
		return e;
	}

	// PERHAPS elem *toElem(IRState *irs);
}
