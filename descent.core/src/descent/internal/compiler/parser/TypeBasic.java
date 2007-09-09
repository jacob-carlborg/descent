package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.MATCH.MATCHconvert;
import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;
import static descent.internal.compiler.parser.TY.Tbit;
import static descent.internal.compiler.parser.TY.Tbool;
import static descent.internal.compiler.parser.TY.Tvoid;

import java.math.BigInteger;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class TypeBasic extends Type {

	private final static BigInteger N_0xFF = new BigInteger("FF", 16);
	private final static BigInteger N_0xFFFF = new BigInteger("FFFF", 16);

	public final static int TFLAGSintegral = 1;
	public final static int TFLAGSfloating = 2;
	public final static int TFLAGSunsigned = 4;
	public final static int TFLAGSreal = 8;
	public final static int TFLAGSimaginary = 0x10;
	public final static int TFLAGScomplex = 0x20;

	public TypeBasic(TY ty) {
		super(ty, null);

		// HACK to get deco ready
		OutBuffer out = new OutBuffer();
		toDecoBuffer(out, null /* it's safe to pass null here */);
		deco = out.extractData();
	}

	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
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
	public int getNodeType() {
		return TYPE_BASIC;
	}

	@Override
	public MATCH implicitConvTo(Type to, SemanticContext context) {
		if (this == to)
			return MATCHexact;

		if (ty == Tvoid || to.ty == Tvoid)
			return MATCHnomatch;
		if (true || context.global.params.Dversion == 1) {
			if (to.ty == Tbool)
				return MATCHnomatch;
		} else {
			if (ty == Tbool || to.ty == Tbool)
				return MATCHnomatch;
		}
		if (to.isTypeBasic() == null)
			return MATCHnomatch;

		TypeBasic tob = (TypeBasic) to;
		if ((ty.flags & TFLAGSintegral) != 0) {
			// Disallow implicit conversion of integers to imaginary or complex
			if ((tob.ty.flags & (TFLAGSimaginary | TFLAGScomplex)) != 0)
				return MATCHnomatch;

			// If converting to integral
			/* TODO semantic
			 if (false && context.global.params.Dversion > 1 && tob.flags & TFLAGSintegral) {
			 d_uns64 sz = size(0);
			 d_uns64 tosz = tob.size(0);

			 if (sz > tosz)
			 return MATCHnomatch;
			 }
			 */
		} else if ((ty.flags & TFLAGSfloating) != 0) {
			// Disallow implicit conversion of floating point to integer
			if ((tob.ty.flags & TFLAGSintegral) != 0)
				return MATCHnomatch;

			Assert.isTrue((tob.ty.flags & TFLAGSfloating) != 0);

			// Disallow implicit conversion from complex to non-complex
			if ((ty.flags & TFLAGScomplex) != 0
					&& (tob.ty.flags & TFLAGScomplex) == 0)
				return MATCHnomatch;

			// Disallow implicit conversion of real or imaginary to complex
			if ((ty.flags & (TFLAGSreal | TFLAGSimaginary)) != 0
					&& (tob.ty.flags & TFLAGScomplex) != 0)
				return MATCHnomatch;

			// Disallow implicit conversion to-from real and imaginary
			if ((ty.flags & (TFLAGSreal | TFLAGSimaginary)) != (tob.ty.flags & (TFLAGSreal | TFLAGSimaginary)))
				return MATCHnomatch;
		}
		return MATCHconvert;
	}

	@Override
	public boolean isbit() {
		return ty == Tbit;
	}

	@Override
	public boolean isintegral() {
		return (ty.flags & TFLAGSintegral) != 0;
	}

	@Override
	public TypeBasic isTypeBasic() {
		return this;
	}

	@Override
	public boolean isunsigned() {
		return (ty.flags & TFLAGSunsigned) != 0;
	}

	public boolean isfloating() {
		return (ty.flags & TFLAGSfloating) != 0;
	}

	public boolean isreal() {
		return (ty.flags & TFLAGSreal) != 0;
	}

	public boolean isimaginary() {
		return (ty.flags & TFLAGSimaginary) != 0;
	}

	public boolean iscomplex() {
		return (ty.flags & TFLAGScomplex) != 0;
	}

	public boolean isscalar() {
		return (ty.flags & (TFLAGSintegral | TFLAGSfloating)) != 0;
	}

	@Override
	public String toString() {
		return ty.name;
	}

	@Override
	public String toChars(SemanticContext context) {
		return toString();
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
	public boolean builtinTypeInfo() {
		return true;
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

}
