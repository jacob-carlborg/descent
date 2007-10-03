package descent.internal.compiler.parser;

import java.math.BigInteger;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.MATCH.MATCHconvert;
import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

import static descent.internal.compiler.parser.TY.Tbit;
import static descent.internal.compiler.parser.TY.Tbool;
import static descent.internal.compiler.parser.TY.Tvoid;

// DMD 1.020
public class TypeBasic extends Type {
	
	public final static integer_t FLT_DIG = new integer_t(6);
	public final static integer_t DBL_DIG = new integer_t(15);
	public final static integer_t LDBL_DIG = DBL_DIG;
	public final static real_t FLT_EPSILON = new real_t(1.192092896e-07F);
	public final static real_t DBL_EPSILON = new real_t(2.2204460492503131e-016);
	public final static real_t LDBL_EPSILON = DBL_EPSILON;
	public final static integer_t FLT_MANT_DIG = new integer_t(24);
	public final static integer_t DBL_MANT_DIG = new integer_t(53);
	public final static integer_t LDBL_MANT_DIG = DBL_MANT_DIG;
	public final static integer_t FLT_MAX_10_EXP = new integer_t(38);
	public final static integer_t DBL_MAX_10_EXP = new integer_t(308);
	public final static integer_t LDBL_MAX_10_EXP = DBL_MAX_10_EXP;
	public final static integer_t FLT_MAX_EXP = new integer_t(128);
	public final static integer_t DBL_MAX_EXP = new integer_t(1024);
	public final static integer_t LDBL_MAX_EXP = DBL_MAX_EXP;
	public final static integer_t FLT_MIN_10_EXP = new integer_t(-37);
	public final static integer_t DBL_MIN_10_EXP = new integer_t(-307);
	public final static integer_t LDBL_MIN_10_EXP = DBL_MIN_10_EXP;
	public final static integer_t FLT_MIN_EXP = new integer_t(-125);
	public final static integer_t DBL_MIN_EXP = new integer_t(-1021);
	public final static integer_t LDBL_MIN_EXP = DBL_MIN_EXP;	

	public TypeBasic(TY ty) {
		super(ty, null);

		// HACK to get deco ready
		OutBuffer out = new OutBuffer();
		toDecoBuffer(out, null /* it's safe to pass null here */);
		deco = out.extractData();
	}

	public TypeBasic(Type singleton) {
		super(singleton.ty, null, singleton);
		this.deco = singleton.deco;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	@Override
	public int alignsize(SemanticContext context) {
		int sz;

		switch (ty) {
		case Tfloat80:
		case Timaginary80:
		case Tcomplex80:
			sz = 2;
			break;

		default:
			sz = size(Loc.ZERO, context);
			break;
		}
		return sz;
	}

	@Override
	public boolean builtinTypeInfo() {
		return true;
	}

	@Override
	public Expression defaultInit(SemanticContext context) {
		BigInteger value;

		switch (ty) {
		case Tchar:
			value = N_0xFF;
			break;

		case Twchar:
		case Tdchar:
			value = N_0xFFFF;
			break;

		case Timaginary32:
		case Timaginary64:
		case Timaginary80:
		case Tfloat32:
		case Tfloat64:
		case Tfloat80:
		case Tcomplex32:
		case Tcomplex64:
		case Tcomplex80:
			return getProperty(Loc.ZERO, Id.nan, 0, 0, context);
		default:
			return new IntegerExp(Loc.ZERO, Id.ZERO, 0, this);
		}
		return new IntegerExp(Loc.ZERO, new integer_t(value), this);
	}

	@Override
	public Expression dotExp(Scope sc, Expression e, IdentifierExp ident,
			SemanticContext context) {
		Type t;

		if (CharOperation.equals(ident.ident, Id.re)) {
			switch (ty) {
			case Tcomplex32:
				t = tfloat32;
				// goto L1;
				e = e.castTo(sc, t, context);
				break;
			case Tcomplex64:
				t = tfloat64;
				// goto L1;
				e = e.castTo(sc, t, context);
				break;
			case Tcomplex80:
				t = tfloat80;
				// goto L1;
				e = e.castTo(sc, t, context);
				break;
			// L1:
			// e = e.castTo(sc, t, context);
			// break;

			case Tfloat32:
			case Tfloat64:
			case Tfloat80:
				break;

			case Timaginary32:
				t = tfloat32;
				// goto L2;
				e = new RealExp(Loc.ZERO, 0.0, t);
				break;
			case Timaginary64:
				t = tfloat64; // goto L2;
				e = new RealExp(Loc.ZERO, 0.0, t);
				break;
			case Timaginary80:
				t = tfloat80; // goto L2;
				e = new RealExp(Loc.ZERO, 0.0, t);
				break;
			// L2:
			// e = new RealExp(Loc.ZERO, 0.0, t);
			// break;

			default:
				return getProperty(e.loc, ident, context);
			}
		} else if (CharOperation.equals(ident.ident, Id.im)) {
			Type t2;

			switch (ty) {
			case Tcomplex32:
				t = timaginary32;
				t2 = tfloat32;
				// goto L3;
				e = e.castTo(sc, t, context);
				e.type = t2;
				break;
			case Tcomplex64:
				t = timaginary64;
				t2 = tfloat64;
				// goto L3;
				e = e.castTo(sc, t, context);
				e.type = t2;
				break;
			case Tcomplex80:
				t = timaginary80;
				t2 = tfloat80;
				// goto L3;
				e = e.castTo(sc, t, context);
				e.type = t2;
				break;
			// L3:
			// e = e.castTo(sc, t, context);
			// e.type = t2;
			// break;

			case Timaginary32:
				t = tfloat32;
				// goto L4;
				e.type = t;
				break;
			case Timaginary64:
				t = tfloat64;
				// goto L4;
				e.type = t;
				break;
			case Timaginary80:
				t = tfloat80;
				// goto L4;
				e.type = t;
				break;
			// L4:
			// e.type = t;
			// break;

			case Tfloat32:
			case Tfloat64:
			case Tfloat80:
				e = new RealExp(Loc.ZERO, 0.0, this);
				break;

			default:
				return getProperty(e.loc, ident, context);
			}
		} else {
			return super.dotExp(sc, e, ident, context);
		}
		return e;
	}

	@Override
	public int getNodeType() {
		return TYPE_BASIC;
	}

	@Override
	public Expression getProperty(Loc loc, char[] ident, int start, int length,
			SemanticContext context) {
		Expression e;
		integer_t ivalue;
		real_t fvalue;

		if (CharOperation.equals(ident, Id.max)) {
			// TODO ensure the Java max/min values are the same as the D ones
			switch (ty) {
			case Tint8:
				ivalue = new integer_t(0x7F);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tuns8:
				ivalue = new integer_t(0xFF);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tint16:
				ivalue = new integer_t(0x7FFF);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tuns16:
				ivalue = new integer_t(0xFFFF);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tint32:
				ivalue = new integer_t(0x7FFFFFFF);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tuns32:
				ivalue = new integer_t(0xFFFFFFFF);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tint64:
				ivalue = new integer_t(new BigInteger("7FFFFFFFFFFFFFFF", 16));
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tuns64:
				ivalue = new integer_t(new BigInteger("FFFFFFFFFFFFFFFF", 16));
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tbit:
				ivalue = new integer_t(1);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tbool:
				ivalue = new integer_t(1);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tchar:
				ivalue = new integer_t(0xFF);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Twchar:
				ivalue = new integer_t(0xFFFF);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tdchar:
				ivalue = new integer_t(0x10FFFF);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;

			case Tcomplex32:
			case Timaginary32:
			case Tfloat32:
				fvalue = new real_t(Float.MAX_VALUE);
				return Lfvalue(loc, fvalue); // goto Lfvalue;
			case Tcomplex64:
			case Timaginary64:
			case Tfloat64:
				fvalue = new real_t(Double.MAX_VALUE);
				return Lfvalue(loc, fvalue); // goto Lfvalue;
			case Tcomplex80:
			case Timaginary80:
			case Tfloat80:
				fvalue = new real_t(0 /* TODO LDBL_MAX */);
				return Lfvalue(loc, fvalue); // goto Lfvalue;
			}
		} else if (CharOperation.equals(ident, Id.min)) {
			switch (ty) {
			case Tint8:
				ivalue = new integer_t(-128);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tuns8:
				ivalue = new integer_t(0);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tint16:
				ivalue = new integer_t(Short.MIN_VALUE);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tuns16:
				ivalue = new integer_t(0);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tint32:
				ivalue = new integer_t(Integer.MIN_VALUE);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tuns32:
				ivalue = new integer_t(0);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tint64:
				ivalue = new integer_t(Long.MIN_VALUE);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tuns64:
				ivalue = new integer_t(0);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tbit:
				ivalue = new integer_t(0);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tbool:
				ivalue = new integer_t(0);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tchar:
				ivalue = new integer_t(0);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Twchar:
				ivalue = new integer_t(0);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;
			case Tdchar:
				ivalue = new integer_t(0);
				return new IntegerExp(loc, ivalue, this); // goto Livalue;

			case Tcomplex32:
			case Timaginary32:
			case Tfloat32:
				fvalue = new real_t(Float.MIN_VALUE);
				return Lfvalue(loc, fvalue); // goto Lfvalue;
			case Tcomplex64:
			case Timaginary64:
			case Tfloat64:
				fvalue = new real_t(Double.MIN_VALUE);
				return Lfvalue(loc, fvalue); // goto Lfvalue;
			case Tcomplex80:
			case Timaginary80:
			case Tfloat80:
				fvalue = new real_t(0 /* TODO LDBL_MIN */);
				return Lfvalue(loc, fvalue); // goto Lfvalue;
			}
		} else if (CharOperation.equals(ident, Id.nan)) {
			switch (ty) {
			case Tcomplex32:
			case Tcomplex64:
			case Tcomplex80:
			case Timaginary32:
			case Timaginary64:
			case Timaginary80:
			case Tfloat32:
			case Tfloat64:
			case Tfloat80: {
				fvalue = new real_t(Double.NaN);
				return Lfvalue(loc, fvalue); // goto Lfvalue;
			}
			}
		} else if (CharOperation.equals(ident, Id.infinity)) {
			switch (ty) {
			case Tcomplex32:
			case Tcomplex64:
			case Tcomplex80:
			case Timaginary32:
			case Timaginary64:
			case Timaginary80:
			case Tfloat32:
			case Tfloat64:
			case Tfloat80:
				fvalue = new real_t(Double.POSITIVE_INFINITY);
				return Lfvalue(loc, fvalue); // goto Lfvalue;
			}
		}

		else if (CharOperation.equals(ident, Id.dig)) {
			switch (ty) {
			case Tcomplex32:
			case Timaginary32:
			case Tfloat32:
				ivalue = FLT_DIG;
				return new IntegerExp(loc, ivalue, Type.tint32); // goto
																	// Lint;
			case Tcomplex64:
			case Timaginary64:
			case Tfloat64:
				ivalue = DBL_DIG;
				return new IntegerExp(loc, ivalue, Type.tint32); // goto
																	// Lint;
			case Tcomplex80:
			case Timaginary80:
			case Tfloat80:
				ivalue = LDBL_DIG;
				return new IntegerExp(loc, ivalue, Type.tint32); // goto
																	// Lint;
			}
		} else if (ident == Id.epsilon) {
			switch (ty) {
			case Tcomplex32:
			case Timaginary32:
			case Tfloat32:
				fvalue = FLT_EPSILON;
				return Lfvalue(loc, fvalue); // goto Lfvalue;
			case Tcomplex64:
			case Timaginary64:
			case Tfloat64:
				fvalue = DBL_EPSILON;
				return Lfvalue(loc, fvalue); // goto Lfvalue;
			case Tcomplex80:
			case Timaginary80:
			case Tfloat80:
				fvalue = LDBL_EPSILON;
				return Lfvalue(loc, fvalue); // goto Lfvalue;
			}
		} else if (ident == Id.mant_dig) {
			switch (ty) {
			case Tcomplex32:
			case Timaginary32:
			case Tfloat32:
				ivalue = FLT_MANT_DIG;
				return new IntegerExp(loc, ivalue, Type.tint32); // goto
																	// Lint;
			case Tcomplex64:
			case Timaginary64:
			case Tfloat64:
				ivalue = DBL_MANT_DIG;
				return new IntegerExp(loc, ivalue, Type.tint32); // goto
																	// Lint;
			case Tcomplex80:
			case Timaginary80:
			case Tfloat80:
				ivalue = LDBL_MANT_DIG;
				return new IntegerExp(loc, ivalue, Type.tint32); // goto
																	// Lint;
			}
		} else if (ident == Id.max_10_exp) {
			switch (ty) {
			case Tcomplex32:
			case Timaginary32:
			case Tfloat32:
				ivalue = FLT_MAX_10_EXP;
				return new IntegerExp(loc, ivalue, Type.tint32); // goto
																	// Lint;
			case Tcomplex64:
			case Timaginary64:
			case Tfloat64:
				ivalue = DBL_MAX_10_EXP;
				return new IntegerExp(loc, ivalue, Type.tint32); // goto
																	// Lint;
			case Tcomplex80:
			case Timaginary80:
			case Tfloat80:
				ivalue = LDBL_MAX_10_EXP;
				return new IntegerExp(loc, ivalue, Type.tint32); // goto
																	// Lint;
			}
		} else if (ident == Id.max_exp) {
			switch (ty) {
			case Tcomplex32:
			case Timaginary32:
			case Tfloat32:
				ivalue = FLT_MAX_EXP;
				return new IntegerExp(loc, ivalue, Type.tint32); // goto
																	// Lint;
			case Tcomplex64:
			case Timaginary64:
			case Tfloat64:
				ivalue = DBL_MAX_EXP;
				return new IntegerExp(loc, ivalue, Type.tint32); // goto
																	// Lint;
			case Tcomplex80:
			case Timaginary80:
			case Tfloat80:
				ivalue = LDBL_MAX_EXP;
				return new IntegerExp(loc, ivalue, Type.tint32); // goto
																	// Lint;
			}
		} else if (ident == Id.min_10_exp) {
			switch (ty) {
			case Tcomplex32:
			case Timaginary32:
			case Tfloat32:
				ivalue = FLT_MIN_10_EXP;
				return new IntegerExp(loc, ivalue, Type.tint32); // goto
																	// Lint;
			case Tcomplex64:
			case Timaginary64:
			case Tfloat64:
				ivalue = DBL_MIN_10_EXP;
				return new IntegerExp(loc, ivalue, Type.tint32); // goto
																	// Lint;
			case Tcomplex80:
			case Timaginary80:
			case Tfloat80:
				ivalue = LDBL_MIN_10_EXP;
				return new IntegerExp(loc, ivalue, Type.tint32); // goto
																	// Lint;
			}
		} else if (ident == Id.min_exp) {
			switch (ty) {
			case Tcomplex32:
			case Timaginary32:
			case Tfloat32:
				ivalue = FLT_MIN_EXP;
				return new IntegerExp(loc, ivalue, Type.tint32); // goto
																	// Lint;
			case Tcomplex64:
			case Timaginary64:
			case Tfloat64:
				ivalue = DBL_MIN_EXP;
				return new IntegerExp(loc, ivalue, Type.tint32); // goto
																	// Lint;
			case Tcomplex80:
			case Timaginary80:
			case Tfloat80:
				ivalue = LDBL_MIN_EXP;
				return new IntegerExp(loc, ivalue, Type.tint32); // goto
																	// Lint;
			}
		}

		return super.getProperty(loc, ident, start, length, context);
	}

	@Override
	public MATCH implicitConvTo(Type to, SemanticContext context) {
		// See explanation of tbasic member
		if (to instanceof TypeBasic
				&& this.singleton == ((TypeBasic) to).singleton) {
			return MATCHexact;
		}

		if (this == to) {
			return MATCHexact;
		}

		if (ty == Tvoid || to.ty == Tvoid) {
			return MATCHnomatch;
		}
		if (true || context.global.params.Dversion == 1) {
			if (to.ty == Tbool) {
				return MATCHnomatch;
			}
		} else {
			if (ty == Tbool || to.ty == Tbool) {
				return MATCHnomatch;
			}
		}
		if (to.isTypeBasic() == null) {
			return MATCHnomatch;
		}

		TypeBasic tob = (TypeBasic) to;
		if ((ty.flags & TFLAGSintegral) != 0) {
			// Disallow implicit conversion of integers to imaginary or complex
			if ((tob.ty.flags & (TFLAGSimaginary | TFLAGScomplex)) != 0) {
				return MATCHnomatch;
			}

			// If converting to integral
			int sz = size(Loc.ZERO, context);
			int tosz = tob.size(Loc.ZERO, context);

		    /* Can't convert to smaller size or, if same size, change sign
		     */
		    if (sz > tosz) {
		    	return MATCHnomatch;
		    }
		} else if ((ty.flags & TFLAGSfloating) != 0) {
			// Disallow implicit conversion of floating point to integer
			if ((tob.ty.flags & TFLAGSintegral) != 0) {
				return MATCHnomatch;
			}

			Assert.isTrue((tob.ty.flags & TFLAGSfloating) != 0);

			// Disallow implicit conversion from complex to non-complex
			if ((ty.flags & TFLAGScomplex) != 0
					&& (tob.ty.flags & TFLAGScomplex) == 0) {
				return MATCHnomatch;
			}

			// Disallow implicit conversion of real or imaginary to complex
			if ((ty.flags & (TFLAGSreal | TFLAGSimaginary)) != 0
					&& (tob.ty.flags & TFLAGScomplex) != 0) {
				return MATCHnomatch;
			}

			// Disallow implicit conversion to-from real and imaginary
			if ((ty.flags & (TFLAGSreal | TFLAGSimaginary)) != (tob.ty.flags & (TFLAGSreal | TFLAGSimaginary))) {
				return MATCHnomatch;
			}
		}
		return MATCHconvert;
	}

	@Override
	public boolean isbit() {
		return ty == Tbit;
	}

	@Override
	public boolean iscomplex() {
		return (ty.flags & TFLAGScomplex) != 0;
	}

	@Override
	public boolean isfloating() {
		return (ty.flags & TFLAGSfloating) != 0;
	}

	@Override
	public boolean isimaginary() {
		return (ty.flags & TFLAGSimaginary) != 0;
	}

	@Override
	public boolean isintegral() {
		return (ty.flags & TFLAGSintegral) != 0;
	}

	@Override
	public boolean isreal() {
		return (ty.flags & TFLAGSreal) != 0;
	}

	@Override
	public boolean isscalar(SemanticContext context) {
		return (ty.flags & (TFLAGSintegral | TFLAGSfloating)) != 0;
	}

	@Override
	public TypeBasic isTypeBasic() {
		return this;
	}

	@Override
	public boolean isunsigned() {
		return (ty.flags & TFLAGSunsigned) != 0;
	}

	@Override
	public boolean isZeroInit(SemanticContext context) {
		switch (ty) {
		case Tchar:
		case Twchar:
		case Tdchar:
		case Timaginary32:
		case Timaginary64:
		case Timaginary80:
		case Tfloat32:
		case Tfloat64:
		case Tfloat80:
		case Tcomplex32:
		case Tcomplex64:
		case Tcomplex80:
			return false; // no
		}
		return true; // yes
	}

	@Override
	public int size(Loc loc, SemanticContext context) {
		int size;

		switch (ty) {
		case Tint8:
		case Tuns8:
			size = 1;
			break;
		case Tint16:
		case Tuns16:
			size = 2;
			break;
		case Tint32:
		case Tuns32:
		case Tfloat32:
		case Timaginary32:
			size = 4;
			break;
		case Tint64:
		case Tuns64:
		case Tfloat64:
		case Timaginary64:
			size = 8;
			break;
		case Tfloat80:
		case Timaginary80:
			size = REALSIZE;
			break;
		case Tcomplex32:
			size = 8;
			break;
		case Tcomplex64:
			size = 16;
			break;
		case Tcomplex80:
			size = REALSIZE * 2;
			break;

		case Tvoid:
			size = 1;
			break;

		case Tbit:
			size = 1;
			break;
		case Tbool:
			size = 1;
			break;
		case Tchar:
			size = 1;
			break;
		case Twchar:
			size = 2;
			break;
		case Tdchar:
			size = 4;
			break;

		default:
			throw new IllegalStateException("assert(0);");
		}
		return size;
	}

	@Override
	public Type syntaxCopy() {
		// No semantic analysis done on basic types, no need to copy
		return this;
	}

	@Override
	public void toCBuffer2(OutBuffer buf, IdentifierExp ident, HdrGenState hgs,
			SemanticContext context) {
		buf.prependstring(this.toString());
		if (ident != null) {
			buf.writeByte(' ');
			buf.writestring(ident.toChars());
		}
	}

	@Override
	public String toChars(SemanticContext context) {
		return toString();
	}

	@Override
	public String toString() {
		if (ty.name != null) {
			return ty.name;
		} else {
			return ty.toString();
		}
	}

	private Expression Lfvalue(Loc loc, real_t fvalue) {
		if (isreal() || isimaginary()) {
			return new RealExp(loc, fvalue, this);
		} else {
			complex_t cvalue = new complex_t(fvalue, fvalue);
			return new ComplexExp(loc, cvalue, this);
		}
	}

	public final static int TFLAGSintegral = 1;
	public final static int TFLAGSfloating = 2;
	public final static int TFLAGSunsigned = 4;
	public final static int TFLAGSreal = 8;
	public final static int TFLAGSimaginary = 0x10;
	public final static int TFLAGScomplex = 0x20;
	private final static BigInteger N_0xFF = new BigInteger("FF", 16);
	private final static BigInteger N_0xFFFF = new BigInteger("FFFF", 16);

}
