package descent.internal.compiler.parser;

import java.math.BigInteger;

// TODO methods for Real may not work
public class NumberUtils {
	
	public static IntegerWrapper castToUns64(IntegerWrapper value) {
		// TODO semantic implement
		return value;
	}

	public static IntegerWrapper castToInt64(IntegerWrapper value) {
		return new IntegerWrapper(value.bigIntegerValue());
	}

	private final static long INT_UPPER = 0xFFFFFFFF + 1;
	public static  IntegerWrapper castToUns32(IntegerWrapper value) {
		long b = value.longValue();
		if (b < 0) {
			b %= INT_UPPER;
			if (b < 0) {
				b += INT_UPPER;
			}
		}
		if (b > INT_UPPER) {
			b %= INT_UPPER;
		}
		return new IntegerWrapper(new BigInteger(String.valueOf(b)));
	}

	public static  IntegerWrapper castToInt32(IntegerWrapper value) {
		return new IntegerWrapper(value.intValue());
	}

	private final static int SHORT_UPPER = 0xFFFF + 1;
	public static  IntegerWrapper castToUns16(IntegerWrapper value) {
		int b = value.intValue();
		if (b < 0) {
			b %= SHORT_UPPER;
			if (b < 0) {
				b += SHORT_UPPER;
			}
		}
		if (b > SHORT_UPPER) {
			b %= SHORT_UPPER;
		}
		return new IntegerWrapper(b);
	}

	public static  IntegerWrapper castToInt16(IntegerWrapper value) {
		return new IntegerWrapper(value.shortValue());
	}

	private final static int BYTE_UPPER = 0xFF + 1;
	public static  IntegerWrapper castToUns8(IntegerWrapper value) {
		short b = value.shortValue();
		if (b < 0) {
			b %= BYTE_UPPER;
			if (b < 0) {
				b += BYTE_UPPER;
			}
		}
		if (b > BYTE_UPPER) {
			b %= BYTE_UPPER;
		}
		return new IntegerWrapper(b);
	}

	public static  IntegerWrapper castToInt8(IntegerWrapper value) {
		return new IntegerWrapper(value.byteValue());
	}
	
//---
	
	public static IntegerWrapper castToUns64(Real value) {
		return castToUns64(value.toIntegerWrapper());
	}

	public static IntegerWrapper castToInt64(Real value) {
		return castToInt64(value.toIntegerWrapper());
	}

	public static  IntegerWrapper castToUns32(Real value) {
		return castToUns32(value.toIntegerWrapper());
	}

	public static  IntegerWrapper castToInt32(Real value) {
		return castToInt32(value.toIntegerWrapper());
	}

	public static  IntegerWrapper castToUns16(Real value) {
		return castToUns16(value.toIntegerWrapper());
	}

	public static  IntegerWrapper castToInt16(Real value) {
		return castToInt16(value.toIntegerWrapper());
	}

	public static  IntegerWrapper castToUns8(Real value) {
		return castToUns8(value.toIntegerWrapper());
	}

	public static  IntegerWrapper castToInt8(Real value) {
		return castToInt8(value.toIntegerWrapper());
	}
	
// ---
	
	public static boolean isTrue(IntegerWrapper value) {
		return value.compareTo(BigInteger.ZERO) != 0;
	}	

}
