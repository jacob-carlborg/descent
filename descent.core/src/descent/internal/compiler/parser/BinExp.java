package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.MATCH.MATCHnomatch;
import static descent.internal.compiler.parser.TOK.TOKadd;
import static descent.internal.compiler.parser.TOK.TOKaddass;
import static descent.internal.compiler.parser.TOK.TOKandass;
import static descent.internal.compiler.parser.TOK.TOKassign;
import static descent.internal.compiler.parser.TOK.TOKcatass;
import static descent.internal.compiler.parser.TOK.TOKdivass;
import static descent.internal.compiler.parser.TOK.TOKin;
import static descent.internal.compiler.parser.TOK.TOKmin;
import static descent.internal.compiler.parser.TOK.TOKminass;
import static descent.internal.compiler.parser.TOK.TOKminusminus;
import static descent.internal.compiler.parser.TOK.TOKmodass;
import static descent.internal.compiler.parser.TOK.TOKmulass;
import static descent.internal.compiler.parser.TOK.TOKnull;
import static descent.internal.compiler.parser.TOK.TOKorass;
import static descent.internal.compiler.parser.TOK.TOKplusplus;
import static descent.internal.compiler.parser.TOK.TOKremove;
import static descent.internal.compiler.parser.TOK.TOKshlass;
import static descent.internal.compiler.parser.TOK.TOKshrass;
import static descent.internal.compiler.parser.TOK.TOKstring;
import static descent.internal.compiler.parser.TOK.TOKushrass;
import static descent.internal.compiler.parser.TOK.TOKxorass;
import static descent.internal.compiler.parser.TY.Tarray;
import static descent.internal.compiler.parser.TY.Tbool;
import static descent.internal.compiler.parser.TY.Tclass;
import static descent.internal.compiler.parser.TY.Terror;
import static descent.internal.compiler.parser.TY.Tpointer;
import static descent.internal.compiler.parser.TY.Tsarray;
import static descent.internal.compiler.parser.TY.Tstruct;
import static descent.internal.compiler.parser.TY.Tvoid;

import java.math.BigInteger;

import org.eclipse.core.runtime.Assert;

public abstract class BinExp extends Expression {

	public Expression e1;
	public Expression e2;

	public BinExp(Loc loc, TOK op, Expression e1, Expression e2) {
		super(loc, op);
		this.e1 = e1;
		this.e2 = e2;
		if (e1 != null && e2 != null) {
			this.start = e1.start;
			this.length = e2.start + e2.length - e1.start;
		}
	}
	
	public Expression BinExp_semantic(Scope sc, SemanticContext context) {
		e1 = e1.semantic(sc, context);
		if (e1.type == null) {
			error("%s has no value", e1.toChars(context));
			e1.type = Type.terror;
		}
		e2 = e2.semantic(sc, context);
		if (e2.type == null) {
			error("%s has no value", e2.toChars(context));
			e2.type = Type.terror;
		}
		Assert.isNotNull(e1.type);
		return this;
	}

	@Override
	public int checkSideEffect(int flag, SemanticContext context) {
		if (op == TOKplusplus || op == TOKminusminus || op == TOKassign
				|| op == TOKaddass || op == TOKminass || op == TOKcatass
				|| op == TOKmulass || op == TOKdivass || op == TOKmodass
				|| op == TOKshlass || op == TOKshrass || op == TOKushrass
				|| op == TOKandass || op == TOKorass || op == TOKxorass
				|| op == TOKin || op == TOKremove) {
			return 1;
		}
		return super.checkSideEffect(flag, context);
	}

	public Expression commonSemanticAssignIntegral(Scope sc,
			SemanticContext context) {
		Expression e;

		if (type == null) {
			BinExp_semantic(sc, context);
			e2 = resolveProperties(sc, e2, context);

			e = op_overload(sc);
			if (e != null) {
				return e;
			}

			e1 = e1.modifiableLvalue(sc, null, context);
			e1.checkScalar(context);
			type = e1.type;
			if (type.toBasetype(context).ty == Tbool) {
				e2 = e2.implicitCastTo(sc, type, context);
			}

			typeCombine(sc, context);
			e1.checkIntegral(context);
			e2.checkIntegral(context);
		}
		return this;
	}

	public Expression commonSemmanticAssign(Scope sc, SemanticContext context) {
		Expression e;

		if (type == null) {
			BinExp_semantic(sc, context);
			e2 = resolveProperties(sc, e2, context);

			e = op_overload(sc);
			if (e != null) {
				return e;
			}

			e1 = e1.modifiableLvalue(sc, null, context);
			e1.checkScalar(context);
			type = e1.type;
			if (type.toBasetype(context).ty == Tbool) {
				error("operator not allowed on bool expression %s", toChars(context));
			}
			typeCombine(sc, context);
			e1.checkArithmetic(context);
			e2.checkArithmetic(context);

			if (op == TOKmodass && e2.type.iscomplex()) {
				error("cannot perform modulo complex arithmetic");
				return new IntegerExp(loc, 0);
			}
		}
		return this;
	}

	public void incompatibleTypes(SemanticContext context) {
		error("incompatible types for ((%s) %s (%s)): '%s' and '%s'", e1
				.toChars(context), op.toString(), e2.toChars(context), e1.type.toChars(context),
				e2.type.toChars(context));
	}

	public boolean isunsigned() {
		return e1.type.isunsigned() || e2.type.isunsigned();
	}

	public Expression scaleFactor(Scope sc, SemanticContext context) {
		BigInteger stride;
		Type t1b = e1.type.toBasetype(context);
		Type t2b = e2.type.toBasetype(context);

		if (t1b.ty == Tpointer && t2b.isintegral()) { // Need to adjust operator by the stride
			// Replace (ptr + int) with (ptr + (int * stride))
			Type t = Type.tptrdiff_t;

			stride = new BigInteger(String.valueOf(t1b.next.size(loc, context)));
			if (!t.equals(t2b)) {
				e2 = e2.castTo(sc, t, context);
			}
			if (t1b.next.isbit()) {
				// BUG: should add runtime check for misaligned offsets
				// This perhaps should be done by rewriting as &p[i]
				// and letting back end do it.
				e2 = new UshrExp(loc, e2, new IntegerExp(loc, 3, t));
			} else {
				e2 = new MulExp(loc, e2, new IntegerExp(loc, new IntegerWrapper(stride), t));
			}
			e2.type = t;
			type = e1.type;
			// TODO check "t2b.ty" 
		} else if (t2b.ty != Tarray && t1b.isintegral()) { // Need to adjust operator by the stride
			// Replace (int + ptr) with (ptr + (int * stride))
			Type t = Type.tptrdiff_t;
			Expression e;

			stride = new BigInteger(String.valueOf(t2b.next.size(loc, context)));
			if (!t.equals(t1b)) {
				e = e1.castTo(sc, t, context);
			} else {
				e = e1;
			}
			if (t2b.next.isbit()) {
				// BUG: should add runtime check for misaligned offsets
				e = new UshrExp(loc, e, new IntegerExp(loc, 3, t));
			} else {
				e = new MulExp(loc, e, new IntegerExp(loc, new IntegerWrapper(stride), t));
			}
			e.type = t;
			type = e2.type;
			e1 = e2;
			e2 = e;
		}
		return this;
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		return BinExp_semantic(sc, context);
	}
	
	public Expression semanticp(Scope sc, SemanticContext context) {
		BinExp_semantic(sc, context);
		e1 = resolveProperties(sc, e1, context);
		e2 = resolveProperties(sc, e2, context);
		return this;
	}

	@Override
	public Expression syntaxCopy() {
		BinExp e;

		e = (BinExp) copy();
		e.type = null;
		e.e1 = e.e1.syntaxCopy();
		e.e2 = e.e2.syntaxCopy();
		return e;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		expToCBuffer(buf, hgs, e1, op.precedence, context);
	    buf.writeByte(' ');
	    buf.writestring(op.toString());
	    buf.writeByte(' ');
	    expToCBuffer(buf, hgs, e2, PREC.values()[op.precedence.ordinal() + 1], context);
	}

	public Expression typeCombine(Scope sc, SemanticContext context) {
		Type t1;
		Type t2;
		Type t;
		TY ty;

		e1 = e1.integralPromotions(sc, context);
		e2 = e2.integralPromotions(sc, context);

		// BUG: do toBasetype()
		t1 = e1.type;
		t2 = e2.type;
		Assert.isNotNull(t1);
		Assert.isNotNull(t2);

		Type t1b = t1.toBasetype(context);
		Type t2b = t2.toBasetype(context);

		ty = Type.impcnvResult[t1b.ty.ordinal()][t2b.ty.ordinal()];
		if (ty != Terror) {
			TY ty1;
			TY ty2;

			ty1 = Type.impcnvType1[t1b.ty.ordinal()][t2b.ty.ordinal()];
			ty2 = Type.impcnvType2[t1b.ty.ordinal()][t2b.ty.ordinal()];

			if (t1b.ty == ty1) // if no promotions
			{
				if (t1 == t2) {
					if (type == null) {
						type = t1;
					}
					return this;
				}

				if (t1b == t2b) {
					if (type == null) {
						type = t1b;
					}
					return this;
				}
			}

			if (type == null) {
				type = Type.basic[ty.ordinal()];
			}

			t1 = Type.basic[ty1.ordinal()];
			t2 = Type.basic[ty2.ordinal()];
			e1 = e1.castTo(sc, t1, context);
			e2 = e2.castTo(sc, t2, context);
			return this;
		}

		t = t1;
		if (t1 == t2) {
			if ((t1.ty == Tstruct || t1.ty == Tclass)
					&& (op == TOKmin || op == TOKadd)) {
				return typeCombine_Lincompatible_End(t, context);
			}
		} else if (t1.isintegral() && t2.isintegral()) {
			int sz1 = t1.size(loc, context);
			int sz2 = t2.size(loc, context);
			boolean sign1 = t1.isunsigned();
			boolean sign2 = t2.isunsigned();

			if (sign1 == sign2) {
				if (sz1 < sz2) {
					// goto Lt2;
					e1 = e1.castTo(sc, t2, context);
					t = t2;
					if (type == null) {
						type = t;
					}
					return this;
				} else {
					// goto Lt1;
					e2 = e2.castTo(sc, t1, context);
					t = t1;
					if (type == null) {
						type = t;
					}
					return this;
				}
			}
			if (!sign1) {
				if (sz1 >= sz2) {
					// goto Lt1;
					e2 = e2.castTo(sc, t1, context);
					t = t1;
					if (type == null) {
						type = t;
					}
					return this;
				} else {
					// goto Lt2
					e1 = e1.castTo(sc, t2, context);
					t = t2;
					if (type == null) {
						type = t;
					}
					return this;
				}
			} else {
				if (sz2 >= sz1) {
					// goto Lt2
					e1 = e1.castTo(sc, t2, context);
					t = t2;
					if (type == null) {
						type = t;
					}
					return this;
				} else {
					// goto Lt1;
					e2 = e2.castTo(sc, t1, context);
					t = t1;
					if (type == null) {
						type = t;
					}
					return this;
				}
			}
		} else if (t1.ty == Tpointer && t2.ty == Tpointer) {
			// Bring pointers to compatible type
			Type t1n = t1.next;
			Type t2n = t2.next;

			assert (t1n != t2n);
			if (t1n.ty == Tvoid) {
				t = t2;
			} else if (t2n.ty == Tvoid) {
				;
			} else if (t1n.ty == Tclass && t2n.ty == Tclass) {
				ClassDeclaration cd1 = t1n.isClassHandle();
				ClassDeclaration cd2 = t2n.isClassHandle();
				int offset[] = { 0 };

				if (cd1.isBaseOf(cd2, offset, context)) {
					if (offset[0] != 0) {
						e2 = e2.castTo(sc, t, context);
					}
				} else if (cd2.isBaseOf(cd1, offset, context)) {
					t = t2;
					if (offset[0] != 0) {
						e1 = e1.castTo(sc, t, context);
					}
				} else {
					return typeCombine_Lincompatible_End(t, context);
				}
			} else {
				return typeCombine_Lincompatible_End(t, context);
			}
		} else if ((t1.ty == Tsarray || t1.ty == Tarray) && e2.op == TOKnull
				&& t2.ty == Tpointer && t2.next.ty == Tvoid) {
			// goto Lx1;
			t = t1.next.arrayOf(context);
			e1 = e1.castTo(sc, t, context);
			e2 = e2.castTo(sc, t, context);
			if (type == null) {
				type = t;
			}
			return this;
		} else if ((t2.ty == Tsarray || t2.ty == Tarray) && e1.op == TOKnull
				&& t1.ty == Tpointer && t1.next.ty == Tvoid) {
			// goto Lx2;
			t = t2.next.arrayOf(context);
			e1 = e1.castTo(sc, t, context);
			e2 = e2.castTo(sc, t, context);
			if (type == null) {
				type = t;
			}
			return this;
		} else if ((t1.ty == Tsarray || t1.ty == Tarray)
				&& t1.implicitConvTo(t2, context) != MATCHnomatch) {
			// goto Lt2;
			e1 = e1.castTo(sc, t2, context);
			t = t2;
			if (type == null) {
				type = t;
			}
			return this;
		} else if ((t2.ty == Tsarray || t2.ty == Tarray)
				&& t2.implicitConvTo(t1, context) != MATCHnomatch) {
			// goto Lt1;
			e2 = e2.castTo(sc, t1, context);
			t = t1;
			if (type == null) {
				type = t;
			}
			return this;
		} else if (t1.ty == Tclass || t2.ty == Tclass) {
			MATCH i1;
			MATCH i2;

			i1 = e2.implicitConvTo(t1, context);
			i2 = e1.implicitConvTo(t2, context);

			if (i1 != MATCHnomatch && i2 != MATCHnomatch) {
				// We have the case of class vs. void*, so pick class
				if (t1.ty == Tpointer) {
					i1 = MATCHnomatch;
				} else if (t2.ty == Tpointer) {
					i2 = MATCHnomatch;
				}
			}

			if (i2 != MATCHnomatch) {
				// goto Lt2;
				e1 = e1.castTo(sc, t2, context);
				t = t2;
				if (type == null) {
					type = t;
				}
				return this;
			} else if (i1 != MATCHnomatch) {
				// goto Lt1;
				e2 = e2.castTo(sc, t1, context);
				t = t1;
				if (type == null) {
					type = t;
				}
				return this;
			} else {
				return typeCombine_Lincompatible_End(t, context);
			}
		} else if ((e1.op == TOKstring || e1.op == TOKnull)
				&& e1.implicitConvTo(t2, context) != MATCHnomatch) {
			// goto Lt2;
			e1 = e1.castTo(sc, t2, context);
			t = t2;
			if (type == null) {
				type = t;
			}
			return this;
		} else if ((e2.op == TOKstring || e2.op == TOKnull)
				&& e2.implicitConvTo(t1, context) != MATCHnomatch) {
			// goto Lt1;
			e2 = e2.castTo(sc, t1, context);
			t = t1;
			if (type == null) {
				type = t;
			}
			return this;
		} else if (t1.ty == Tsarray
				&& t2.ty == Tsarray
				&& e2.implicitConvTo(t1.next.arrayOf(context), context) != MATCHnomatch) {
			t = t1.next.arrayOf(context);
			e1 = e1.castTo(sc, t, context);
			e2 = e2.castTo(sc, t, context);
		} else if (t1.ty == Tsarray
				&& t2.ty == Tsarray
				&& e1.implicitConvTo(t2.next.arrayOf(context), context) != MATCHnomatch) {
			t = t2.next.arrayOf(context);
			e1 = e1.castTo(sc, t, context);
			e2 = e2.castTo(sc, t, context);
		} else {
			incompatibleTypes(context);
		}
		if (type == null) {
			type = t;
		}
		return this;
	}

	private Expression typeCombine_Lincompatible_End(Type t, SemanticContext context) {
		incompatibleTypes(context);
		if (type == null) {
			type = t;
		}
		return this;
	}

}
