package descent.internal.compiler.parser;

import java.util.List;
import static descent.internal.compiler.parser.TY.*;
import static descent.internal.compiler.parser.MATCH.*;

public class ArrayLiteralExp extends Expression {

	public List<Expression> elements;

	public ArrayLiteralExp(List<Expression> elements) {
		super(TOK.TOKarrayliteral);
		this.elements = elements;
	}

	@Override
	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		Type typeb = type.toBasetype(context);
		Type tb = t.toBasetype(context);
		if ((tb.ty == Tarray || tb.ty == Tsarray)
				&& (typeb.ty == Tarray || typeb.ty == Tsarray)) {
			if (tb.ty == Tsarray) {
				TypeSArray tsa = (TypeSArray) tb;
				if (elements.size() != tsa.dim.toInteger(context).intValue()) {
					// goto L1;
					return super.castTo(sc, t, context);
				}
			}

			for (int i = 0; i < elements.size(); i++) {
				Expression e = elements.get(i);
				e = e.castTo(sc, tb.next, context);
				elements.set(i, e);
			}
			type = t;
			return this;
		}
		if (tb.ty == Tpointer && typeb.ty == Tsarray) {
			type = typeb.next.pointerTo(context);
		}
		// L1:
		return super.castTo(sc, t, context);
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
		expandTuples(elements);
		for (int i = 0; i < elements.size(); i++) {
			e = elements.get(i);

			if (e.type == null) {
				error("%s has no value", e.toChars());
			}
			e = resolveProperties(sc, e, context);
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
			elements.set(i, e);
		}

		if (t0 == null) {
			t0 = Type.tvoid;
		}
		type = new TypeSArray(t0, new IntegerExp(elements.size()));
		type = type.semantic(sc, context);
		return this;
	}

	@Override
	public Expression syntaxCopy() {
		return new ArrayLiteralExp(arraySyntaxCopy(elements));
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
		buf.printf("A" + dim);
		for (int i = 0; i < dim; i++) {
			Expression e = elements.get(i);
			e.toMangleBuffer(buf, context);
		}
	}

}
