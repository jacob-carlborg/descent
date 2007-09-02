package descent.internal.compiler.parser;

import java.math.BigInteger;

// TODO methods for Real may not work
public class NumberUtils {
	
	public static integer_t castToUns64(integer_t value) {
		// TODO semantic implement
		return value;
	}

	public static integer_t castToInt64(integer_t value) {
		return new integer_t(value.bigIntegerValue());
	}

	private final static long INT_UPPER = 0xFFFFFFFFL + 1;
	public static  integer_t castToUns32(integer_t value) {
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
		return new integer_t(new BigInteger(String.valueOf(b)));
	}

	public static  integer_t castToInt32(integer_t value) {
		return new integer_t(value.intValue());
	}

	private final static int SHORT_UPPER = 0xFFFF + 1;
	public static  integer_t castToUns16(integer_t value) {
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
		return new integer_t(b);
	}

	public static  integer_t castToInt16(integer_t value) {
		return new integer_t(value.shortValue());
	}

	private final static int BYTE_UPPER = 0xFF + 1;
	public static  integer_t castToUns8(integer_t value) {
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
		return new integer_t(b);
	}

	public static  integer_t castToInt8(integer_t value) {
		return new integer_t(value.byteValue());
	}
	
//---
	
	public static integer_t castToUns64(real_t value) {
		return castToUns64(value.toIntegerWrapper());
	}

	public static integer_t castToInt64(real_t value) {
		return castToInt64(value.toIntegerWrapper());
	}

	public static  integer_t castToUns32(real_t value) {
		return castToUns32(value.toIntegerWrapper());
	}

	public static  integer_t castToInt32(real_t value) {
		return castToInt32(value.toIntegerWrapper());
	}

	public static  integer_t castToUns16(real_t value) {
		return castToUns16(value.toIntegerWrapper());
	}

	public static  integer_t castToInt16(real_t value) {
		return castToInt16(value.toIntegerWrapper());
	}

	public static  integer_t castToUns8(real_t value) {
		return castToUns8(value.toIntegerWrapper());
	}

	public static  integer_t castToInt8(real_t value) {
		return castToInt8(value.toIntegerWrapper());
	}
	
// ---
	
	public static boolean isTrue(integer_t value) {
		return value.compareTo(BigInteger.ZERO) != 0;
	}	

}
