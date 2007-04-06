package descent.internal.compiler.parser;

import java.math.BigInteger;

public class BigIntegerUtils {
	
	public static BigInteger castToUns64(BigInteger value) {
		// TODO semantic implement
		return value;
	}

	public static BigInteger castToInt64(BigInteger value) {
		return new BigInteger(String.valueOf(value.longValue()));
	}

	private final static long INT_UPPER = 0xFFFFFFFF + 1;
	public static  BigInteger castToUns32(BigInteger value) {
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
		return new BigInteger(String.valueOf(b));
	}

	public static  BigInteger castToInt32(BigInteger value) {
		return new BigInteger(String.valueOf(value.intValue()));
	}

	private final static int SHORT_UPPER = 0xFFFF + 1;
	public static  BigInteger castToUns16(BigInteger value) {
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
		return new BigInteger(String.valueOf(b));
	}

	public static  BigInteger castToInt16(BigInteger value) {
		return new BigInteger(String.valueOf(value.shortValue()));
	}

	private final static int BYTE_UPPER = 0xFF + 1;
	public static  BigInteger castToUns8(BigInteger value) {
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
		return new BigInteger(String.valueOf(b));
	}

	public static  BigInteger castToInt8(BigInteger value) {
		return new BigInteger(String.valueOf(value.byteValue()));
	}
	
	public static boolean isTrue(BigInteger value) {
		return value.compareTo(BigInteger.ZERO) != 0;
	}

}
