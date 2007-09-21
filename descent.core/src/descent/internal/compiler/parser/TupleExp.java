package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.DYNCAST.DYNCAST_DSYMBOL;
import static descent.internal.compiler.parser.DYNCAST.DYNCAST_EXPRESSION;
import static descent.internal.compiler.parser.DYNCAST.DYNCAST_TYPE;

// DMD 1.020
public class TupleExp extends Expression {

	public Expressions exps;

	public TupleExp(Loc loc, Expressions exps) {
		super(loc, TOK.TOKtuple);
		this.exps = exps;
		this.type = null;
	}

	public TupleExp(Loc loc, TupleDeclaration tup, SemanticContext context) {
		super(loc, TOK.TOKtuple);
		exps = new Expressions(tup.objects.size());
		type = null;

		for (int i = 0; i < tup.objects.size(); i++) {
			ASTDmdNode o = tup.objects.get(i);
			if (o.dyncast() == DYNCAST_EXPRESSION) {
				Expression e = (Expression) o;
				e = e.syntaxCopy();
				exps.add(e);
			} else if (o.dyncast() == DYNCAST_DSYMBOL) {
				Dsymbol s = (Dsymbol) o;
				Expression e = new DsymbolExp(loc, s);
				exps.add(e);
			} else if (o.dyncast() == DYNCAST_TYPE) {
				Type t = (Type) o;
				Expression e = new TypeExp(loc, t);
				exps.add(e);
			} else {
				context.acceptProblem(Problem.newSemanticTypeWarning(IProblem.SymbolNotAnExpression, 0, o.start, o.length, new String[] { o.toChars(context) }));
			}
		}
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exps);
		}
		visitor.endVisit(this);
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
	public Expression doInline(InlineDoState ids) {
		TupleExp ce;

		ce = (TupleExp) copy();
		ce.exps = arrayExpressiondoInline(exps, ids);
		return ce;
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
	public int inlineCost(InlineCostState ics, SemanticContext context) {
		return 1 + arrayInlineCost(ics, exps, context);
	}

	@Override
	public Expression inlineScan(InlineScanState iss, SemanticContext context) {
		Expression e = this;

		arrayInlineScan(iss, exps, context);

		return e;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		Expressions expsx = null;

		for (int i = 0; i < exps.size(); i++) {
			Expression e = exps.get(i);
			Expression ex;

			ex = e.interpret(istate, context);
			if (ex == EXP_CANT_INTERPRET) {
				expsx = null;
				return ex;
			}

			/* If any changes, do Copy On Write
			 */
			if (ex != e) {
				if (null == expsx) {
					expsx = new Expressions(exps.size());
					for (int j = 0; j < i; j++) {
						expsx.set(j, exps.get(j));
					}
				}
				expsx.set(i, ex);
			}
		}
		if (expsx != null) {
			TupleExp te = new TupleExp(loc, expsx);
			expandTuples(te.exps, context);
			te.type = new TypeTuple(te.exps);
			return te;
		}
		return this;
	}

	@Override
	public Expression optimize(int result, SemanticContext context) {
		for (int i = 0; i < exps.size(); i++) {
			Expression e = exps.get(i);

			e = e.optimize(WANTvalue | (result & WANTinterpret), context);
			exps.set(i, e);
		}
		return this;
	}

	@Override
	public void scanForNestedRef(Scope sc, SemanticContext context) {
		arrayExpressionScanForNestedRef(sc, exps, context);
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
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolHasNoValue, 0, e.start, e.length, new String[] { e.toChars(context) }));
				e.type = Type.terror;
			}
			exps.set(i, e);
		}

		expandTuples(exps, context);
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

	//PERHAPS int inlineCost(InlineCostState *ics);
	//PERHAPS Expression *doInline(InlineDoState *ids);
	//PERHAPS Expression *inlineScan(InlineScanState *iss);
}
