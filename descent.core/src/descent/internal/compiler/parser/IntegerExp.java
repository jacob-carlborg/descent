package descent.internal.compiler.parser;

import java.math.BigInteger;

public class IntegerExp extends Expression {
	
	private final static BigInteger N_0x8000000000000000 = new BigInteger("8000000000000000", 16);
	private final static BigInteger N_0xFFFFFFFF80000000 = new BigInteger("FFFFFFFF80000000", 16);

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
	
	private BigInteger cast(BigInteger num, TY ty) {
		// TODO implement cast in BigInteger
		return num;
	}
	
	@Override
	public int getNodeType() {
		return INTEGER_EXP;
	}

}
