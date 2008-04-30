package descent.internal.compiler.parser;

// DMD 1.020
import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

import static descent.internal.compiler.parser.TY.*;
import static descent.internal.compiler.parser.TY.Tpointer;
import static descent.internal.compiler.parser.TY.Tsarray;

public class ArrayLiteralExp extends Expression {

	public Expressions elements, sourceElements;

	public ArrayLiteralExp(Loc loc, Expressions elements) {
		super(loc, TOK.TOKarrayliteral);
		this.elements = elements;
		if (this.elements != null) {
			this.sourceElements = new Expressions(elements);
		}
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceElements);
		}
		visitor.endVisit(this);
	}

	@Override
	public Expression castTo(Scope sc, Type t, SemanticContext context) {
	    if (same(type, t, context)) {
	    	return this;
	    }
	    
	    ArrayLiteralExp e = this;		
		Type typeb = type.toBasetype(context);
		Type tb = t.toBasetype(context);
	    if ((tb.ty == Tarray || tb.ty == Tsarray) &&
	    		(typeb.ty == Tarray || typeb.ty == Tsarray) &&
	    		tb.nextOf().toBasetype(context).ty != Tvoid) {
			if (tb.ty == Tsarray) {
				TypeSArray tsa = (TypeSArray) tb;
				if (elements.size() != tsa.dim.toInteger(context).intValue()) {
					// goto L1;
					return e.Expression_castTo(sc, t, context);
				}
			}

			e = (ArrayLiteralExp) copy();
			e.elements = (Expressions) elements.copy();
			for (int i = 0; i < elements.size(); i++) {
				Expression ex = elements.get(i);
				ex = ex.castTo(sc, tb.next, context);
				e.elements.set(i, ex);
			}
			e.type = t;
			return e;
		}
		if (tb.ty == Tpointer && typeb.ty == Tsarray) {
			e = (ArrayLiteralExp) copy();
			e.type = typeb.nextOf().pointerTo(context);
		}
		// L1:
		return e.Expression_castTo(sc, t, context);
	}

	@Override
	public int checkSideEffect(int flag, SemanticContext context) {
		int f = 0;

		for (int i = 0; i < elements.size(); i++) {
			Expression e = elements.get(i);

			f |= e.checkSideEffect(2, context);
		}
		if (flag == 0 && f == 0) {
			super.checkSideEffect(0, context);
		}
		return f;
	}

	@Override
	public int getNodeType() {
		return ARRAY_LITERAL_EXP;
	}

	@Override
	public MATCH implicitConvTo(Type t, SemanticContext context) {
		MATCH result = MATCHexact;

		Type typeb = type.toBasetype(context);
		Type tb = t.toBasetype(context);
		if ((tb.ty == Tarray || tb.ty == Tsarray)
				&& (typeb.ty == Tarray || typeb.ty == Tsarray)) {
			if (tb.ty == Tsarray) {
				TypeSArray tsa = (TypeSArray) tb;
				if (elements.size() != tsa.dim.toInteger(context).intValue()) {
					result = MATCHnomatch;
				}
			}

			for (int i = 0; i < elements.size(); i++) {
				Expression e = elements.get(i);
				MATCH m = e.implicitConvTo(tb.next, context);
				if (m.ordinal() < result.ordinal()) {
					result = m; // remember worst match
				}
				if (result == MATCHnomatch) {
					break; // no need to check for worse
				}
			}
			return result;
		} else {
			return super.implicitConvTo(t, context);
		}
	}

	@Override
	public boolean isBool(boolean result) {
		int dim = elements != null ? elements.size() : 0;
		return result ? (dim != 0) : (dim == 0);
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression e;
		Type t0 = null;

		// Run semantic() on each element
		for (int i = 0; i < elements.size(); i++) {
			e = elements.get(i);
			e = e.semantic(sc, context);
			elements.set(i, e);
		}
		expandTuples(elements, context);
		for (int i = 0; i < elements.size(); i++) {
			e = elements.get(i);

			if (e.type == null) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolHasNoValue, e, new String[] { e.toChars(context) }));
			}
			e = resolveProperties(sc, e, context);

			boolean committed = true;
			if (e.op == TOK.TOKstring)
				committed = ((StringExp) e).committed;

			if (t0 == null) {
				t0 = e.type;
				// Convert any static arrays to dynamic arrays
				if (t0.ty == Tsarray) {
					t0 = t0.next.arrayOf(context);
					e = e.implicitCastTo(sc, t0, context);
				}
			} else {
				e = e.implicitCastTo(sc, t0, context);
			}

			if (!committed && e.op == TOK.TOKstring) {
				StringExp se = (StringExp) e;
				se.committed = false;
			}

			elements.set(i, e);
		}

		if (t0 == null) {
			t0 = Type.tvoid;
		}
		// PERHAPS singleton
		type = new TypeSArray(t0, new IntegerExp(loc, elements.size()), context.encoder);
		type = type.semantic(loc, sc, context);
		return this;
	}

	@Override
	public Expression syntaxCopy(SemanticContext context) {
		return new ArrayLiteralExp(loc, arraySyntaxCopy(elements, context));
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writeByte('[');
		argsToCBuffer(buf, elements, hgs, context);
		buf.writeByte(']');
	}

	@Override
	public void toMangleBuffer(OutBuffer buf, SemanticContext context) {
		int dim = elements != null ? elements.size() : 0;
		buf.writestring("A");
		buf.writestring(dim);
		for (int i = 0; i < dim; i++) {
			Expression e = elements.get(i);
			e.toMangleBuffer(buf, context);
		}
	}

	@Override
	public void scanForNestedRef(Scope sc, SemanticContext context) {
		arrayExpressionScanForNestedRef(sc, elements, context);
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		Expressions expsx = null;

		if (null != elements) {
			for (int i = 0; i < elements.size(); i++) {
				Expression e = (Expression) elements.get(i);
				Expression ex;

				ex = e.interpret(istate, context);
				if (ex == EXP_CANT_INTERPRET) {
					return EXP_CANT_INTERPRET;
				}

				/*
				 * If any changes, do Copy On Write
				 */
				if (ex != e) {
					if (null == expsx) {
						expsx = new Expressions();
						expsx.addAll(elements);
					}
					expsx.set(i, ex);
				}
			}
		}

		if (null != elements && null != expsx) {
			expandTuples(expsx, context);
			if (expsx.size() != elements.size()) {
				return EXP_CANT_INTERPRET;
			}
			ArrayLiteralExp ae = new ArrayLiteralExp(loc, expsx);
			ae.type = type;
			return ae;
		}
		return this;
	}

	@Override
	public Expression optimize(int result, SemanticContext context) {
		if (null != elements) {
			for (int i = 0; i < elements.size(); i++) {
				Expression e = elements.get(i);

				e = e.optimize(WANTvalue | (result & WANTinterpret), context);
				elements.set(i, e);
			}
		}
		return this;
	}
}
