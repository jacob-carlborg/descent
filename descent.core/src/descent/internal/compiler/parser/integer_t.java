package descent.internal.compiler.parser;

import java.math.BigInteger;

public class integer_t extends Number {
	
	private final static long INT_UPPER = 0xFFFFFFFFL + 1;
	private final static int SHORT_UPPER = 0xFFFF + 1;
	private final static int BYTE_UPPER = 0xFF + 1;

	public final static integer_t ZERO = new integer_t(0);
	public final static integer_t ONE = new integer_t(1);

	private static final long serialVersionUID = 1L;

	private int intValue;
	private BigInteger bigIntegerValue;

	public integer_t(BigInteger bigIntegerValue) {
		this.bigIntegerValue = bigIntegerValue;
	}

	public integer_t(int intValue) {
		this.intValue = intValue;
	}
	
	// add

	public integer_t add(BigInteger value) {
		if (bigIntegerValue == null) {
			return new integer_t(toBigInteger(intValue).add(value));
		} else {
			return new integer_t(bigIntegerValue.add(value));
		}
	}

	public integer_t add(int value) {
		if (bigIntegerValue == null) {
			// TODO integer_t optimize
			return new integer_t(toBigInteger(intValue).add(toBigInteger(value)));
		} else {
			return new integer_t(bigIntegerValue.add(toBigInteger(value)));
		}
	}

	public integer_t add(integer_t value) {
		if (value.bigIntegerValue == null) {
			return add(value.intValue);
		} else {
			return add(value.bigIntegerValue);
		}
	}
	
	// and

	public integer_t and(BigInteger value) {
		if (bigIntegerValue == null) {
			return new integer_t(toBigInteger(intValue).and(value));
		} else {
			return new integer_t(bigIntegerValue.and(value));
		}
	}
	
	public integer_t and(int value) {
		if (bigIntegerValue == null) {
			return new integer_t(intValue & value);
		} else {
			return new integer_t(bigIntegerValue.and(toBigInteger(value)));
		}
	}
	
	public integer_t and(integer_t value) {
		if (value.bigIntegerValue == null) {
			return and(value.intValue);
		} else {
			return and(value.bigIntegerValue);
		}
	}
	
	// compareTo

	public int compareTo(BigInteger value) {
		if (bigIntegerValue == null) {
			return toBigInteger(intValue).compareTo(value);
		} else {
			return bigIntegerValue.compareTo(value);
		}
	}

	public int compareTo(int value) {
		if (bigIntegerValue == null) {
			return intValue > value ? 1 : (intValue < value ? -1 : 0);
		} else {
			return bigIntegerValue.compareTo(toBigInteger(value));
		}
	}

	public int compareTo(integer_t value) {
		if (value.bigIntegerValue == null) {
			return compareTo(value.intValue);
		} else {
			return compareTo(value.bigIntegerValue);
		}
	}
	
	// complement

	public integer_t complement() {
		if (bigIntegerValue == null) {
			return new integer_t(~intValue);
		} else {
			return new integer_t(bigIntegerValue.not());
		}			
	}
	
	// divide

	public integer_t divide(BigInteger value) {
		if (bigIntegerValue == null) {
			return new integer_t(toBigInteger(intValue).divide(value));
		} else {
			return new integer_t(bigIntegerValue.divide(value));
		}
	}
	
	public integer_t divide(int value) {
		if (bigIntegerValue == null) {
			return new integer_t(intValue / value);
		} else {
			return new integer_t(bigIntegerValue.divide(toBigInteger(value)));
		}
	}
	
	public integer_t divide(integer_t value) {
		if (value.bigIntegerValue == null) {
			return divide(value.intValue);
		} else {
			return divide(value.bigIntegerValue);
		}
	}
	
	// equals
	
	public boolean equals(BigInteger value) {
		if (bigIntegerValue == null) {
			return toBigInteger(intValue).equals(value);
		} else {
			return bigIntegerValue.equals(value);
		}
	}
	
	public boolean equals(int value) {
		if (bigIntegerValue == null) {
			return intValue == value;
		} else {
			return bigIntegerValue.equals(toBigInteger(value));
		}
	}
	
	public boolean equals(integer_t value) {
		if (value.bigIntegerValue == null) {
			return equals(value.intValue);
		} else {
			return equals(value.bigIntegerValue);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Integer) {
			return equals(((Integer) other).intValue());
		} else if (other instanceof BigInteger) {
			return equals((BigInteger) other);
		} else if (other instanceof integer_t) {
			return equals((integer_t) other);
		} else {
			return false;
		}
	}
	
	// mod

	public integer_t mod(BigInteger value) {
		if (bigIntegerValue == null) {
			return new integer_t(toBigInteger(intValue).mod(value));
		} else {
			return new integer_t(bigIntegerValue.mod(value));
		}
	}
	
	public integer_t mod(int value) {
		if (bigIntegerValue == null) {
			return new integer_t(intValue % value);
		} else {
			return new integer_t(bigIntegerValue.mod(toBigInteger(value)));
		}
	}
	
	public integer_t mod(integer_t value) {
		if (value.bigIntegerValue == null) {
			return mod(value.intValue);
		} else {
			return mod(value.bigIntegerValue);
		}
	}
	
	// multiply

	public integer_t multiply(BigInteger value) {
		if (bigIntegerValue == null) {
			return new integer_t(toBigInteger(intValue).multiply(value));
		} else {
			return new integer_t(bigIntegerValue.multiply(value));
		}
	}
	
	public integer_t multiply(int value) {
		if (bigIntegerValue == null) {
			// TODO integer_t optimize
			return new integer_t(toBigInteger(intValue).multiply(toBigInteger(value)));
		} else {
			return new integer_t(bigIntegerValue.multiply(toBigInteger(value)));
		}
	}
	
	public integer_t multiply(integer_t value) {
		if (value.bigIntegerValue == null) {
			return multiply(value.intValue);
		} else {
			return multiply(value.bigIntegerValue);
		}
	}
	
	// negate
	
	public integer_t negate() {
		if (bigIntegerValue == null) {
			return new integer_t(-intValue);
		} else {
			return new integer_t(bigIntegerValue.negate());
		}
	}
	
	// or

	public integer_t or(BigInteger value) {
		if (bigIntegerValue == null) {
			return new integer_t(toBigInteger(intValue).or(value));
		} else {
			return new integer_t(bigIntegerValue.or(value));
		}
	}
	
	public integer_t or(int value) {
		if (bigIntegerValue == null) {
			return new integer_t(intValue | value);
		} else {
			return new integer_t(bigIntegerValue.or(toBigInteger(value)));
		}
	}
	
	public integer_t or(integer_t value) {
		if (value.bigIntegerValue == null) {
			return or(value.intValue);
		} else {
			return or(value.bigIntegerValue);
		}
	}
	
	// shiftLeft
	
	public integer_t shiftLeft(BigInteger value) {
		if (bigIntegerValue == null) {
			// TODO integer_t this may not work if value is bigger than int
			return new integer_t(toBigInteger(intValue).shiftLeft(value.intValue()));
		} else {
			return new integer_t(bigIntegerValue.shiftLeft(value.intValue()));
		}
	}
	
	public integer_t shiftLeft(int value) {
		if (bigIntegerValue == null) {
			return new integer_t(intValue << value);
		} else {
			return new integer_t(bigIntegerValue.shiftLeft(value));
		}
	}
	
	public integer_t shiftLeft(integer_t value) {
		if (value.bigIntegerValue == null) {
			return shiftLeft(value.intValue);
		} else {
			return shiftLeft(value.bigIntegerValue);
		}
	}
	
	// shiftRight
	
	public integer_t shiftRight(BigInteger value) {
		if (bigIntegerValue == null) {
			// TODO integer_t this may not work if value is bigger than int
			return new integer_t(toBigInteger(intValue).shiftRight(value.intValue()));
		} else {
			return new integer_t(bigIntegerValue.shiftRight(value.intValue()));
		}
	}
	
	public integer_t shiftRight(int value) {
		if (bigIntegerValue == null) {
			// TODO integer_t optimize
			return new integer_t(intValue << value);
		} else {
			return new integer_t(bigIntegerValue.shiftRight(value));
		}
	}
	
	public integer_t shiftRight(integer_t value) {
		if (value.bigIntegerValue == null) {
			return shiftRight(value.intValue);
		} else {
			return shiftRight(value.bigIntegerValue);
		}
	}
	
	// subtract

	public integer_t subtract(BigInteger value) {
		if (bigIntegerValue == null) {
			return new integer_t(toBigInteger(intValue).subtract(value));
		} else {
			return new integer_t(bigIntegerValue.subtract(value));
		}
	}
	
	public integer_t subtract(int value) {
		if (bigIntegerValue == null) {
			// TODO integer_t optimize
			return new integer_t(toBigInteger(intValue).subtract(toBigInteger(value)));
		} else {
			return new integer_t(bigIntegerValue.subtract(toBigInteger(value)));
		}
	}
	
	public integer_t subtract(integer_t value) {
		if (value.bigIntegerValue == null) {
			return subtract(value.intValue);
		} else {
			return subtract(value.bigIntegerValue);
		}
	}
	
	// unsignedShiftRight
	
	public integer_t unsignedShiftRight(BigInteger value) {
		if (bigIntegerValue == null) {
			return new integer_t(intValue >>> value.intValue());
		} else {
			return new integer_t(bigIntegerValue.intValue() >>> value.intValue());
		}
	}
	
	public integer_t unsignedShiftRight(int value) {
		if (bigIntegerValue == null) {
			return new integer_t(intValue >>> value);
		} else {
			// TODO integer_t implement
			return new integer_t(intValue() >>> value);
		}
	}
	
	public integer_t unsignedShiftRight(integer_t value) {
		if (value.bigIntegerValue == null) {
			return unsignedShiftRight(value.intValue);
		} else {
			return unsignedShiftRight(value.bigIntegerValue);
		}
	}
	
	// xor

	public integer_t xor(BigInteger value) {
		if (bigIntegerValue == null) {
			return new integer_t(toBigInteger(intValue).xor(value));
		} else {
			return new integer_t(bigIntegerValue.xor(value));
		}
	}
	
	public integer_t xor(int value) {
		if (bigIntegerValue == null) {
			return new integer_t(intValue ^ value);
		} else {
			return new integer_t(bigIntegerValue.xor(toBigInteger(value)));
		}
	}
	
	public integer_t xor(integer_t value) {
		if (value.bigIntegerValue == null) {
			return xor(value.intValue);
		} else {
			return xor(value.bigIntegerValue);
		}
	}
	
	// casts
	
	public integer_t castToUns64() {
		// TODO integer_t implement
		return this;
	}
	
	public integer_t castToInt64() {
		if (bigIntegerValue == null) {
			return this;
		} else {
			return new integer_t(this.bigIntegerValue());
		}
	}
	
	public integer_t castToUns32() {
		long b = this.longValue();
		if (b < 0) {
			b %= INT_UPPER;
			if (b < 0) {
				b += INT_UPPER;
			}
		}
		if (b > INT_UPPER) {
			b %= INT_UPPER;
		}
		return new integer_t(toBigInteger(b));
	}
	
	public integer_t castToInt32() {
		return new integer_t(this.intValue());
	}
	
	public integer_t castToUns16() {
		int b = this.intValue();
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
	
	public integer_t castToInt16() {
		return new integer_t(this.shortValue());
	}
	
	public integer_t castToUns8() {
		short b = this.shortValue();
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
	
	public integer_t castToInt8() {
		return new integer_t(this.byteValue());
	}
	
	// isTrue
	
	public boolean isTrue() {
		if (bigIntegerValue == null) {
			return intValue != 0;
		} else {
			return bigIntegerValue.compareTo(BigInteger.ZERO) != 0;
		}
	}
	
	// xxxValue
	
	public BigInteger bigIntegerValue() {
		if (bigIntegerValue != null) {
			return bigIntegerValue;
		} else {
			return BigInteger.valueOf(intValue);
		}
	}

	@Override
	public double doubleValue() {
		if (bigIntegerValue != null) {
			return bigIntegerValue.doubleValue();
		} else {
			return intValue;
		}
	}

	@Override
	public float floatValue() {
		if (bigIntegerValue != null) {
			return bigIntegerValue.floatValue();
		} else {
			return intValue;
		}
	}

	@Override
	public int intValue() {
		if (bigIntegerValue != null) {
			return bigIntegerValue.intValue();
		} else {
			return intValue;
		}
	}

	@Override
	public long longValue() {
		if (bigIntegerValue != null) {
			return bigIntegerValue.longValue();
		} else {
			return intValue;
		}
	}
	
	@Override
	public String toString() {
		if (bigIntegerValue != null) {
			return bigIntegerValue.toString();
		} else {
			return String.valueOf(intValue);
		}
	}
	
	private static BigInteger toBigInteger(long value) {
		return BigInteger.valueOf(value);
	}
	
	private static BigInteger toBigInteger(int value) {
		return BigInteger.valueOf(value);
	}
	

}
