package descent.internal.compiler.parser;

import java.util.List;

public class TupleExp extends Expression {

	public List<Expression> exps;

	public TupleExp(Loc loc, List<Expression> exps) {
		super(loc, TOK.TOKtuple);
		this.exps = exps;
		this.type = null;
	}

	@Override
	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		for (int i = 0; i < exps.size(); i++) {
			Expression e = exps.get(i);
			e = e.castTo(sc, t, context);
			exps.set(i, e);
		}
		return this;
	}

	@Override
	public void checkEscape(SemanticContext context) {
		for (int i = 0; i < exps.size(); i++) {
			Expression e = exps.get(i);
			e.checkEscape(context);
		}
	}

	@Override
	public int checkSideEffect(int flag, SemanticContext context) {
		int f = 0;

		for (int i = 0; i < exps.size(); i++) {
			Expression e = exps.get(i);

			f |= e.checkSideEffect(2, context);
		}
		if (flag == 0 && f == 0) {
			super.checkSideEffect(0, context);
		}
		return f;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o instanceof Expression) {
			if (((Expression) o).op == TOK.TOKtuple) {
				TupleExp te = (TupleExp) o;
				if (exps.size() != te.exps.size()) {
					return false;
				}
				for (int i = 0; i < exps.size(); i++) {
					Expression e1 = exps.get(i);
					Expression e2 = te.exps.get(i);

					if (!e1.equals(e2)) {
						return false;
					}
				}
				return true;
			}
		}

		return false;
	}

	@Override
	public int getNodeType() {
		return TUPLE_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (type != null) {
			return this;
		}

		// Run semantic() on each argument
		for (int i = 0; i < exps.size(); i++) {
			Expression e = exps.get(i);

			e = e.semantic(sc, context);
			if (e.type == null) {
				error("%s has no value", e.toChars());
				e.type = Type.terror;
			}
			exps.set(i, e);
		}

		expandTuples(exps);
		if (false && exps.size() == 1) {
			return exps.get(0);
		}
		type = TypeTuple.newExpressions(exps, context);
		return this;
	}

	@Override
	public Expression syntaxCopy() {
		return new TupleExp(loc, arraySyntaxCopy(exps));
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("tuple(");
		argsToCBuffer(buf, exps, hgs, context);
		buf.writeByte(')');
	}

}
