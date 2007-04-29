package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.MATCH.MATCHconvert;
import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;
import static descent.internal.compiler.parser.TY.Tenum;
import static descent.internal.compiler.parser.TY.Tint32;
import static descent.internal.compiler.parser.TY.Tuns32;
import static descent.internal.compiler.parser.TY.Tuns64;

import java.math.BigInteger;

import org.eclipse.core.runtime.Assert;

public class IntegerExp extends Expression {

	private final static BigInteger N_0x8000000000000000 = new BigInteger(
			"8000000000000000", 16);
	private final static BigInteger N_0xFFFFFFFF80000000 = new BigInteger(
			"FFFFFFFF80000000", 16);
	private final static BigInteger N_0xFF = new BigInteger("FF", 16);
	private final static BigInteger N_0xFFFF = new BigInteger("FFFF", 16);
	private final static BigInteger N_0xFFFFFFFF = new BigInteger("FFFFFFFF",
			16);
	private final static BigInteger N_0x10FFFF = new BigInteger("10FFFF", 16);
	private final static BigInteger N_SLASH_SLASH = new BigInteger("197");

	public String str;
	public BigInteger value;

	public IntegerExp(Loc loc, BigInteger value) {
		this(loc, value.toString(), value, Type.tint32);
	}

	public IntegerExp(Loc loc, BigInteger value, Type type) {
		this(loc, null, value, type);
	}

	public IntegerExp(Loc loc, int value) {
		this(loc, new BigInteger(String.valueOf(value)));
	}

	public IntegerExp(Loc loc, int value, Type type) {
		this(loc, new BigInteger(String.valueOf(value)), type);
	}

	public IntegerExp(Loc loc, String str, BigInteger value, Type type) {
		super(loc, TOK.TOKint64);
		this.str = str;
		this.value = value;
		this.type = type;
	}

	public IntegerExp(Loc loc, String str, int value, Type type) {
		this(loc, str, new BigInteger(String.valueOf(value)), type);
	}

	private BigInteger cast(BigInteger num, TY ty) {
		// TODO implement cast in BigInteger
		return num;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o instanceof Expression) {
			if (((Expression) o).op == TOK.TOKint64) {
				IntegerExp ne = (IntegerExp) o;
				return type.equals(ne.type) && value.equals(ne.value);
			}
		}

		return false;
	}

	@Override
	public int getNodeType() {
		return INTEGER_EXP;
	}

	@Override
	public MATCH implicitConvTo(Type t, SemanticContext context) {
		if (type.equals(t)) {
			return MATCHexact;
		}

		TY ty = type.toBasetype(context).ty;
		TY toty = t.toBasetype(context).ty;

		if (type.implicitConvTo(t, context) == MATCHnomatch && t.ty == Tenum) {
			return MATCHnomatch;
		}

		switch (ty) {
		case Tbit:
		case Tbool:
			value = value.and(BigInteger.ONE);
			ty = Tint32;
			break;

		case Tint8:
			value = BigIntegerUtils.castToInt8(value);
			ty = Tint32;
			break;

		case Tchar:
		case Tuns8:
			value = value.and(N_0xFF);
			ty = Tint32;
			break;

		case Tint16:
			value = BigIntegerUtils.castToInt16(value);
			ty = Tint32;
			break;

		case Tuns16:
		case Twchar:
			value = value.and(N_0xFFFF);
			ty = Tint32;
			break;

		case Tint32:
			value = BigIntegerUtils.castToInt32(value);
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
		switch (toty) {
		case Tbit:
		case Tbool:
			if (!(value.and(BigInteger.ONE)).equals(value)) {
				return MATCHnomatch;
			}
			return MATCHconvert;

		case Tint8:
			if (!BigIntegerUtils.castToInt8(value).equals(value)) {
				return MATCHnomatch;
			}
			return MATCHconvert;

		case Tchar:
		case Tuns8:
			if (!BigIntegerUtils.castToUns8(value).equals(value)) {
				return MATCHnomatch;
			}
			return MATCHconvert;

		case Tint16:
			if (!BigIntegerUtils.castToInt16(value).equals(value)) {
				return MATCHnomatch;
			}
			return MATCHconvert;

		case Tuns16:
			if (!BigIntegerUtils.castToUns16(value).equals(value)) {
				return MATCHnomatch;
			}
			return MATCHconvert;

		case Tint32:
			if (ty == Tuns32) {
			} else if (!BigIntegerUtils.castToInt32(value).equals(value)) {
				return MATCHnomatch;
			}
			return MATCHconvert;

		case Tuns32:
			if (ty == Tint32) {
			} else if (!BigIntegerUtils.castToUns32(value).equals(value)) {
				return MATCHnomatch;
			}
			return MATCHconvert;

		case Tdchar:
			if (value.compareTo(N_0x10FFFF) > 0) {
				return MATCHnomatch;
			}
			return MATCHconvert;

		case Twchar:
			if (!cast(value, ty).equals(value)) {
				return MATCHnomatch;
			}
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

	@Override
	public boolean isBool(boolean result) {
		return result ? value.compareTo(BigInteger.ZERO) != 0 : value
				.compareTo(BigInteger.ZERO) == 0;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (type == null) {
			// Determine what the type of this number is
			BigInteger number = value;

			if (number.compareTo(N_0x8000000000000000) >= 0) {
				type = Type.tuns64;
			} else if (number.compareTo(N_0xFFFFFFFF80000000) >= 0) {
				type = Type.tint64;
			} else {
				type = Type.tint32;
			}
		} else {
			type = type.semantic(loc, sc, context);
		}
		return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		BigInteger v = toInteger(context);

		if (type != null) {
			Type t = type;

			boolean loop = true;
			L1: while (loop) {
				loop = false;
				switch (t.ty) {
				case Tenum: {
					TypeEnum te = (TypeEnum) t;
					buf.printf("cast(" + te.sym.toChars() + "%s)");
					t = te.sym.memtype;
					// goto L1;
					loop = true;
					continue;
				}

				case Ttypedef: {
					TypeTypedef tt = (TypeTypedef) t;
					buf.printf("cast(" + tt.sym.toChars() + ")");
					t = tt.sym.basetype;
					// goto L1;
					loop = true;
					continue;
				}

				case Twchar: // BUG: need to cast(wchar)
				case Tdchar: // BUG: need to cast(dchar)
					if (BigIntegerUtils.castToUns64(v).compareTo(N_0xFF) > 0) {
						/* TODO semantic
						 buf.printf("'\\U%08x'", v);
						 */
						break;
					}
				case Tchar:
					if (Chars.isprint(v) && v.compareTo(N_SLASH_SLASH) != 0) {
						buf.printf("'" + BigIntegerUtils.castToInt32(v) + "'");
					} else {
						/* TODO semantic
						 buf.printf("'\\x%02x'", (int)v);
						 */
					}
					break;

				case Tint8:
					buf.writestring("cast(byte)");
					buf.printf(BigIntegerUtils.castToInt32(v).toString());
					break;

				case Tint16:
					buf.writestring("cast(short)");
					buf.printf(BigIntegerUtils.castToInt32(v).toString());
					break;

				case Tint32:
					// L2:
					buf.printf(BigIntegerUtils.castToInt32(v).toString());
					break;

				case Tuns8:
					buf.writestring("cast(ubyte)");
					buf.printf(BigIntegerUtils.castToUns32(v) + "u");
					break;

				case Tuns16:
					buf.writestring("cast(ushort)");
					buf.printf(BigIntegerUtils.castToUns32(v) + "u");
					break;

				case Tuns32:
					// L3:
					buf.printf(BigIntegerUtils.castToUns32(v) + "u");
					break;

				case Tint64:
					/* TODO semantic
					 buf.printf("%jdL", v);
					 */
					break;

				case Tuns64:
					/* TODO semantic
					 buf.printf("%juLU", v);
					 */
					break;

				case Tbit:
				case Tbool:
					buf.writestring(BigIntegerUtils.isTrue(v) ? "true"
							: "false");
					break;

				case Tpointer:
					buf.writestring("cast(");
					buf.writestring(t.toChars());
					buf.writeByte(')');
					buf.printf(BigIntegerUtils.castToUns32(v) + "u");
					break;

				default:
					Assert.isTrue(false);
				}
			}
		} else if (v.and(N_0x8000000000000000).compareTo(BigInteger.ZERO) != 0) {
			buf.printf("0x" + v.toString(16));
		} else {
			buf.printf(v.toString());
		}
	}

	@Override
	public String toChars() {
		return str;
	}

	@Override
	public Complex toComplex(SemanticContext context) {
		return new Complex(toReal(context), Real.ZERO);
	}

	@Override
	public Real toImaginary(SemanticContext context) {
		return Real.ZERO;
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
				value = BigIntegerUtils.castToInt8(value);
				break;
			case Tchar:
			case Tuns8:
				value = BigIntegerUtils.castToUns8(value);
				break;
			case Tint16:
				value = BigIntegerUtils.castToInt16(value);
				break;
			case Twchar:
			case Tuns16:
				value = BigIntegerUtils.castToUns16(value);
				break;
			case Tint32:
				value = BigIntegerUtils.castToInt32(value);
				break;
			case Tpointer:
			case Tdchar:
			case Tuns32:
				value = BigIntegerUtils.castToUns32(value);
				break;
			case Tint64:
				value = BigIntegerUtils.castToInt64(value);
				break;
			case Tuns64:
				value = BigIntegerUtils.castToUns64(value);
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
				Assert.isTrue(false);
			}
			break;
		}
		return value;
	}

	@Override
	public Expression toLvalue(Scope sc, Expression e, SemanticContext context) {
		if (e == null) {
			e = this;
		} else if (loc.filename == null) {
			loc = e.loc;
		}
		e.error("constant %s is not an lvalue", e.toChars());
		return this;
	}

	@Override
	public Real toReal(SemanticContext context) {
		Type t;

		toInteger(context);
		t = type.toBasetype(context);
		if (t.ty == Tuns64) {
			return new Real(BigIntegerUtils.castToUns64(value));
		} else {
			return new Real(BigIntegerUtils.castToInt64(value));
		}
	}

}
