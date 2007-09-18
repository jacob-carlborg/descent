package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TOK.TOKarray;
import static descent.internal.compiler.parser.TY.Tclass;
import static descent.internal.compiler.parser.TY.Tstruct;
import static descent.internal.compiler.parser.Constfold.*;

public abstract class UnaExp extends Expression {

	public Expression e1;

	public UnaExp(Loc loc, TOK op, Expression e1) {
		super(loc, op);
		this.e1 = e1;
	}

	@Override
	public Expression doInline(InlineDoState ids) {
		UnaExp ue = (UnaExp) copy();

		ue.e1 = e1.doInline(ids);
		return ue;
	}

	@Override
	public int inlineCost(InlineCostState ics, SemanticContext context) {
		return 1 + e1.inlineCost(ics, context);
	}

	public Expression interpretCommon(InterState istate,
			UnaExp_fp fp, SemanticContext context) {
		Expression e;
		Expression e1;

		e1 = this.e1.interpret(istate, context);
		if (e1 == EXP_CANT_INTERPRET) {
			// goto Lcant;
			return EXP_CANT_INTERPRET;
		}
		if (!e1.isConst()) {
			// goto Lcant;
			return EXP_CANT_INTERPRET;
		}

		e = fp.call(type, e1, context);
		return e;

		// Lcant:
		// 	return EXP_CANT_INTERPRET;
	}

	@Override
	public Expression op_overload(Scope sc, SemanticContext context) {
		AggregateDeclaration ad = null;
		Dsymbol fd;
		Type t1 = e1.type.toBasetype(context);

		if (t1.ty == Tclass) {
			ad = ((TypeClass) t1).sym;
		} else if (t1.ty == Tstruct) {
			ad = ((TypeStruct) t1).sym;
		} else {
			return null;
		}

		fd = search_function(ad, opId(), context);
		if (fd != null) {
			if (op == TOKarray) {
				Expression e;
				ArrayExp ae = (ArrayExp) this;

				e = new DotIdExp(loc, e1, fd.ident);
				e = new CallExp(loc, e, ae.arguments);
				e = e.semantic(sc, context);
				return e;
			} else {
				// Rewrite +e1 as e1.add()
				return build_overload(loc, sc, e1, null, fd.ident, context);
			}
		}
		return null;
	}

	@Override
	public Expression optimize(int result, SemanticContext context) {
		e1 = e1.optimize(result, context);
		return this;
	}

	@Override
	public void scanForNestedRef(Scope sc, SemanticContext context) {
		e1.scanForNestedRef(sc, context);
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		e1 = e1.semantic(sc, context);
		return this;
	}

	@Override
	public Expression syntaxCopy() {
		UnaExp e;

		e = (UnaExp) copy();
		e.type = null;
		e.e1 = e.e1.syntaxCopy();
		return e;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring(op.toString());
		expToCBuffer(buf, hgs, e1, op.precedence, context);
	}
}
