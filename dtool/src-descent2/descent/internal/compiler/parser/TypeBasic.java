package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.MATCH.MATCHconvert;
import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;
import static descent.internal.compiler.parser.TY.Tbool;
import static descent.internal.compiler.parser.TY.Tbit;
import static descent.internal.compiler.parser.TY.Tvoid;

import java.math.BigInteger;

import org.eclipse.core.runtime.Assert;

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
		toDecoBuffer(out);
		deco = out.extractData();
	}

	@Override
	public Expression defaultInit(SemanticContext context) {
		BigInteger value = BigInteger.ZERO;

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
			return getProperty(Loc.ZERO, Id.nan, context);
		}
		return new IntegerExp(Loc.ZERO, value.toString(), value, this);
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
			if ((ty.flags & TFLAGScomplex) != 0 && (tob.ty.flags & TFLAGScomplex) == 0)
				return MATCHnomatch;

			// Disallow implicit conversion of real or imaginary to complex
			if ((ty.flags & (TFLAGSreal | TFLAGSimaginary)) != 0 && (tob.ty.flags
					& TFLAGScomplex) != 0)
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

}
