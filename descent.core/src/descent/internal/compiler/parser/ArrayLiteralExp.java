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

}
