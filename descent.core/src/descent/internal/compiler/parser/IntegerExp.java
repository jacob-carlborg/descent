package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.MATCH.MATCHconvert;
import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;
import static descent.internal.compiler.parser.TY.Tenum;
import static descent.internal.compiler.parser.TY.Tint32;
import static descent.internal.compiler.parser.TY.Tuns32;

import java.math.BigInteger;

public class IntegerExp extends Expression {
	
	private final static BigInteger N_0x8000000000000000 = new BigInteger("8000000000000000", 16);
	private final static BigInteger N_0xFFFFFFFF80000000 = new BigInteger("FFFFFFFF80000000", 16);
	private final static BigInteger N_0xFF = new BigInteger("FF", 16);
	private final static BigInteger N_0xFFFF = new BigInteger("FFFF", 16);
	private final static BigInteger N_0xFFFFFFFF = new BigInteger("FFFFFFFF", 16);
	private final static BigInteger N_0x10FFFF = new BigInteger("10FFFF", 16);

	public String str;
	public BigInteger value;
	
	public IntegerExp(int value, Type type) {
		this(new BigInteger(String.valueOf(value)), type);
	}
	
	public IntegerExp(BigInteger value, Type type) {
		this(null, value, type);
	}
	
	public IntegerExp(String str, BigInteger value, Type type) {
		super(TOK.TOKint64);
		this.str = str;
		this.value = value;
		this.type = type;
	}
	
	public IntegerExp(String str, int value, Type type) {
		this(str, new BigInteger(String.valueOf(value)), type);
	}
	
	public IntegerExp(BigInteger value) {
		this(value.toString(), value, Type.tint32);
	}
	
	public IntegerExp(int value) {
		this(new BigInteger(String.valueOf(value)));
	}
	
	@Override
	public boolean isBool(boolean result) {
		return result ? value.compareTo(BigInteger.ZERO) != 0 : value.compareTo(BigInteger.ZERO) == 0;
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (type == null) {
			// Determine what the type of this number is
			BigInteger number = value;

			if (number.compareTo(N_0x8000000000000000) >= 0)
				type = Type.tuns64;
			else if (number.compareTo(N_0xFFFFFFFF80000000) >= 0)
				type = Type.tint64;
			else
				type = Type.tint32;
		} else {
			type = type.semantic(sc, context);
		}
		return this;
	}
	
	@Override
	public BigInteger toInteger(SemanticContext context) {
		Type t;

		t = type;
		while (t != null) {
			switch (t.ty) {
			case Tbit:
			case Tbool:
				value = (value.compareTo(BigInteger.ZERO) != 0) ? BigInteger.ONE
						: BigInteger.ZERO;
				break;
			case Tint8:
			case Tchar:
			case Tuns8:
			case Tint16:
			case Twchar:
			case Tuns16:
			case Tint32:
			case Tpointer:
			case Tdchar:
			case Tuns32:
			case Tint64:
			case Tuns64:
				value = cast(value, t.ty);
				break;

			case Tenum: {
				TypeEnum te = (TypeEnum) t;
				t = te.sym.memtype;
				continue;
			}

			case Ttypedef: {
				TypeTypedef tt = (TypeTypedef) t;
				t = tt.sym.basetype;
				continue;
			}

			default:
				throw new IllegalStateException();
			}
			break;
		}
		return value;
	}
	
	// TODO check the original source file to finish
	@Override
	public MATCH implicitConvTo(Type t, SemanticContext context) {
	    if (type.equals(t))
		return MATCHexact;

	    TY ty = type.toBasetype(context).ty;
	    TY toty = t.toBasetype(context).ty;

	    if (type.implicitConvTo(t, context) == MATCHnomatch && t.ty == Tenum)
		return MATCHnomatch;

	    switch (ty)
	    {
		case Tbit:
		case Tbool:
			value = value.and(BigInteger.ONE);
		    ty = Tint32;
		    break;

		case Tint8:
		    value = cast(value, ty);
		    ty = Tint32;
		    break;

		case Tchar:
		case Tuns8:
		    value = value.and(N_0xFF);
		    ty = Tint32;
		    break;

		case Tint16:
			value = cast(value, ty);
		    ty = Tint32;
		    break;

		case Tuns16:
		case Twchar:
			value = value.and(N_0xFFFF);
		    ty = Tint32;
		    break;

		case Tint32:
			value = cast(value, ty);
		    break;

		case Tuns32:
		case Tdchar:
			value = value.and(N_0xFFFFFFFF);
		    ty = Tuns32;
		    break;

		default:
		    break;
	    }

	    // Only allow conversion if no change in value
	    switch (toty)
	    {
		case Tbit:
		case Tbool:
		    if (!(value.and(BigInteger.ONE)).equals(value))
		    	return MATCHnomatch;
		    return MATCHconvert;

		case Tint8:
		    if (!cast(value, ty).equals(value))
		    	return MATCHnomatch;
		    return MATCHconvert;

		case Tchar:
		case Tuns8:
		    if (!cast(value, ty).equals(value))
		    	return MATCHnomatch;
		    return MATCHconvert;

		case Tint16:
		    if (!cast(value, ty).equals(value))
		    	return MATCHnomatch;
		    return MATCHconvert;

		case Tuns16:
		    if (!cast(value, ty).equals(value))
		    	return MATCHnomatch;
		    return MATCHconvert;

		case Tint32:
		    if (ty == Tuns32)
		    {
		    }
		    else if (!cast(value, ty).equals(value))
		    	return MATCHnomatch;
		    return MATCHconvert;

		case Tuns32:
		    if (ty == Tint32)
		    {
		    }
		    else if (!cast(value, ty).equals(value))
		    	return MATCHnomatch;
		    return MATCHconvert;

		case Tdchar:
		    if (value.compareTo(N_0x10FFFF) > 0)
		    	return MATCHnomatch;
		    return MATCHconvert;

		case Twchar:
		    if (!cast(value, ty).equals(value))
		    	return MATCHnomatch;
		    return MATCHconvert;

		    /* TODO semantic
		case Tfloat32:
		{
		    volatile float f;
		    if (type.isunsigned())
		    {
			f = (float)value;
			if (f != value)
			    goto Lno;
		    }
		    else
		    {
			f = (float)(long long)value;
			if (f != (long long)value)
			    goto Lno;
		    }
		    goto Lyes;
		}

		case Tfloat64:
		{
		    volatile double f;
		    if (type.isunsigned())
		    {
			f = (double)value;
			if (f != value)
			    goto Lno;
		    }
		    else
		    {
			f = (double)(long long)value;
			if (f != (long long)value)
			    goto Lno;
		    }
		    goto Lyes;
		}

		case Tfloat80:
		{
		    volatile long double f;
		    if (type.isunsigned())
		    {
			f = (long double)value;
			if (f != value)
			    goto Lno;
		    }
		    else
		    {
			f = (long double)(long long)value;
			if (f != (long long)value)
			    goto Lno;
		    }
		    goto Lyes;
		}
		*/
	    }
	    return super.implicitConvTo(t, context);
	}
	
	private BigInteger cast(BigInteger num, TY ty) {
		// TODO implement cast in BigInteger
		return num;
	}
	
	@Override
	public int getNodeType() {
		return INTEGER_EXP;
	}

}
