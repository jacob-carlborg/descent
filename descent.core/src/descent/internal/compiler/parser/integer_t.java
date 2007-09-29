package descent.internal.compiler.parser;

import java.math.BigInteger;

// DMD 1.020
// This is also an alias of uinteger_t, as defined by DMD
public class integer_t extends Number {
	
	private final static long INT_UPPER = 0xFFFFFFFFL + 1;
	private final static int SHORT_UPPER = 0xFFFF + 1;
	private final static int BYTE_UPPER = 0xFF + 1;

	public final static integer_t ZERO = new integer_t(0);
	public final static integer_t ONE = new integer_t(1);

	private static final long serialVersionUID = 1L;

	private long longValue;
	private BigInteger bigIntegerValue;

	public integer_t(BigInteger bigIntegerValue) {
		this.bigIntegerValue = bigIntegerValue;
	}

	public integer_t(long intValue) {
		this.longValue = intValue;
	}
	
	// add

	public integer_t add(BigInteger value) {
		if (bigIntegerValue == null) {
			return new integer_t(toBigInteger(longValue).add(value));
		} else {
			return new integer_t(bigIntegerValue.add(value));
		}
	}

	public integer_t add(long value) {
		if (bigIntegerValue == null) {
			// TODO integer_t optimize
			return new integer_t(toBigInteger(longValue).add(toBigInteger(value)));
		} else {
			return new integer_t(bigIntegerValue.add(toBigInteger(value)));
		}
	}

	public integer_t add(integer_t value) {
		if (value.bigIntegerValue == null) {
			return add(value.longValue);
		} else {
			return add(value.bigIntegerValue);
		}
	}
	
	// and

	public integer_t and(BigInteger value) {
		if (bigIntegerValue == null) {
			return new integer_t(toBigInteger(longValue).and(value));
		} else {
			return new integer_t(bigIntegerValue.and(value));
		}
	}
	
	public integer_t and(long value) {
		if (bigIntegerValue == null) {
			return new integer_t(longValue & value);
		} else {
			return new integer_t(bigIntegerValue.and(toBigInteger(value)));
		}
	}
	
	public integer_t and(integer_t value) {
		if (value.bigIntegerValue == null) {
			return and(value.longValue);
		} else {
			return and(value.bigIntegerValue);
		}
	}
	
	// compareTo

	public int compareTo(BigInteger value) {
		if (bigIntegerValue == null) {
			return toBigInteger(longValue).compareTo(value);
		} else {
			return bigIntegerValue.compareTo(value);
		}
	}

	public int compareTo(long value) {
		if (bigIntegerValue == null) {
			return longValue > value ? 1 : (longValue < value ? -1 : 0);
		} else {
			return bigIntegerValue.compareTo(toBigInteger(value));
		}
	}

	public int compareTo(integer_t value) {
		if (value.bigIntegerValue == null) {
			return compareTo(value.longValue);
		} else {
			return compareTo(value.bigIntegerValue);
		}
	}
	
	// complement

	public integer_t complement() {
		if (bigIntegerValue == null) {
			return new integer_t(~longValue);
		} else {
			return new integer_t(bigIntegerValue.not());
		}			
	}
	
	// divide

	public integer_t divide(BigInteger value) {
		if (bigIntegerValue == null) {
			return new integer_t(toBigInteger(longValue).divide(value));
		} else {
			return new integer_t(bigIntegerValue.divide(value));
		}
	}
	
	public integer_t divide(long value) {
		if (bigIntegerValue == null) {
			return new integer_t(longValue / value);
		} else {
			return new integer_t(bigIntegerValue.divide(toBigInteger(value)));
		}
	}
	
	public integer_t divide(integer_t value) {
		if (value.bigIntegerValue == null) {
			return divide(value.longValue);
		} else {
			return divide(value.bigIntegerValue);
		}
	}
	
	// equals
	
	public boolean equals(BigInteger value) {
		if (bigIntegerValue == null) {
			return toBigInteger(longValue).equals(value);
		} else {
			return bigIntegerValue.equals(value);
		}
	}
	
	public boolean equals(long value) {
		if (bigIntegerValue == null) {
			return longValue == value;
		} else {
			return bigIntegerValue.equals(toBigInteger(value));
		}
	}
	
	public boolean equals(integer_t value) {
		if (value.bigIntegerValue == null) {
			return equals(value.longValue);
		} else {
			return equals(value.bigIntegerValue);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Integer) {
			return equals(((Integer) other).longValue());
		} else if (other instanceof Long) {
			return equals(((Long) other).longValue());
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
			return new integer_t(toBigInteger(longValue).mod(value));
		} else {
			return new integer_t(bigIntegerValue.mod(value));
		}
	}
	
	public integer_t mod(long value) {
		if (bigIntegerValue == null) {
			return new integer_t(longValue % value);
		} else {
			return new integer_t(bigIntegerValue.mod(toBigInteger(value)));
		}
	}
	
	public integer_t mod(integer_t value) {
		if (value.bigIntegerValue == null) {
			return mod(value.longValue);
		} else {
			return mod(value.bigIntegerValue);
		}
	}
	
	// multiply

	public integer_t multiply(BigInteger value) {
		if (bigIntegerValue == null) {
			return new integer_t(toBigInteger(longValue).multiply(value));
		} else {
			return new integer_t(bigIntegerValue.multiply(value));
		}
	}
	
	public integer_t multiply(long value) {
		if (bigIntegerValue == null) {
			// TODO integer_t optimize
			return new integer_t(toBigInteger(longValue).multiply(toBigInteger(value)));
		} else {
			return new integer_t(bigIntegerValue.multiply(toBigInteger(value)));
		}
	}
	
	public integer_t multiply(integer_t value) {
		if (value.bigIntegerValue == null) {
			return multiply(value.longValue);
		} else {
			return multiply(value.bigIntegerValue);
		}
	}
	
	// negate
	
	public integer_t negate() {
		if (bigIntegerValue == null) {
			return new integer_t(-longValue);
		} else {
			return new integer_t(bigIntegerValue.negate());
		}
	}
	
	// or

	public integer_t or(BigInteger value) {
		if (bigIntegerValue == null) {
			return new integer_t(toBigInteger(longValue).or(value));
		} else {
			return new integer_t(bigIntegerValue.or(value));
		}
	}
	
	public integer_t or(long value) {
		if (bigIntegerValue == null) {
			return new integer_t(longValue | value);
		} else {
			return new integer_t(bigIntegerValue.or(toBigInteger(value)));
		}
	}
	
	public integer_t or(integer_t value) {
		if (value.bigIntegerValue == null) {
			return or(value.longValue);
		} else {
			return or(value.bigIntegerValue);
		}
	}
	
	// shiftLeft
	
	public integer_t shiftLeft(BigInteger value) {
		if (bigIntegerValue == null) {
			// TODO integer_t this may not work if value is bigger than int
			return new integer_t(toBigInteger(longValue).shiftLeft(value.intValue()));
		} else {
			return new integer_t(bigIntegerValue.shiftLeft(value.intValue()));
		}
	}
	
	public integer_t shiftLeft(long value) {
		if (bigIntegerValue == null) {
			return new integer_t(longValue << value);
		} else {
			// TODO integer_t this cast may be wrong
			return new integer_t(bigIntegerValue.shiftLeft((int) value));
		}
	}
	
	public integer_t shiftLeft(integer_t value) {
		if (value.bigIntegerValue == null) {
			return shiftLeft(value.longValue);
		} else {
			return shiftLeft(value.bigIntegerValue);
		}
	}
	
	// shiftRight
	
	public integer_t shiftRight(BigInteger value) {
		if (bigIntegerValue == null) {
			// TODO integer_t this may not work if value is bigger than int
			return new integer_t(toBigInteger(longValue).shiftRight(value.intValue()));
		} else {
			return new integer_t(bigIntegerValue.shiftRight(value.intValue()));
		}
	}
	
	public integer_t shiftRight(long value) {
		if (bigIntegerValue == null) {
			// TODO integer_t optimize
			return new integer_t(longValue << value);
		} else {
			// TODO integer_t this cast may be wrong
			return new integer_t(bigIntegerValue.shiftRight((int) value));
		}
	}
	
	public integer_t shiftRight(integer_t value) {
		if (value.bigIntegerValue == null) {
			return shiftRight(value.longValue);
		} else {
			return shiftRight(value.bigIntegerValue);
		}
	}
	
	// subtract

	public integer_t subtract(BigInteger value) {
		if (bigIntegerValue == null) {
			return new integer_t(toBigInteger(longValue).subtract(value));
		} else {
			return new integer_t(bigIntegerValue.subtract(value));
		}
	}
	
	public integer_t subtract(long value) {
		if (bigIntegerValue == null) {
			// TODO integer_t optimize
			return new integer_t(toBigInteger(longValue).subtract(toBigInteger(value)));
		} else {
			return new integer_t(bigIntegerValue.subtract(toBigInteger(value)));
		}
	}
	
	public integer_t subtract(integer_t value) {
		if (value.bigIntegerValue == null) {
			return subtract(value.longValue);
		} else {
			return subtract(value.bigIntegerValue);
		}
	}
	
	// unsignedShiftRight
	
	public integer_t unsignedShiftRight(BigInteger value) {
		if (bigIntegerValue == null) {
			return new integer_t(longValue >>> value.intValue());
		} else {
			return new integer_t(bigIntegerValue.intValue() >>> value.intValue());
		}
	}
	
	public integer_t unsignedShiftRight(long value) {
		if (bigIntegerValue == null) {
			return new integer_t(longValue >>> value);
		} else {
			// TODO integer_t implement
			return new integer_t(intValue() >>> value);
		}
	}
	
	public integer_t unsignedShiftRight(integer_t value) {
		if (value.bigIntegerValue == null) {
			return unsignedShiftRight(value.longValue);
		} else {
			return unsignedShiftRight(value.bigIntegerValue);
		}
	}
	
	// xor

	public integer_t xor(BigInteger value) {
		if (bigIntegerValue == null) {
			return new integer_t(toBigInteger(longValue).xor(value));
		} else {
			return new integer_t(bigIntegerValue.xor(value));
		}
	}
	
	public integer_t xor(long value) {
		if (bigIntegerValue == null) {
			return new integer_t(longValue ^ value);
		} else {
			return new integer_t(bigIntegerValue.xor(toBigInteger(value)));
		}
	}
	
	public integer_t xor(integer_t value) {
		if (value.bigIntegerValue == null) {
			return xor(value.longValue);
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
			return longValue != 0;
		} else {
			return bigIntegerValue.compareTo(BigInteger.ZERO) != 0;
		}
	}
	
	// xxxValue
	
	public BigInteger bigIntegerValue() {
		if (bigIntegerValue != null) {
			return bigIntegerValue;
		} else {
			return BigInteger.valueOf(longValue);
		}
	}

	@Override
	public double doubleValue() {
		if (bigIntegerValue != null) {
			return bigIntegerValue.doubleValue();
		} else {
			return longValue;
		}
	}

	@Override
	public float floatValue() {
		if (bigIntegerValue != null) {
			return bigIntegerValue.floatValue();
		} else {
			return longValue;
		}
	}

	@Override
	public int intValue() {
		if (bigIntegerValue != null) {
			return bigIntegerValue.intValue();
		} else {
			return (int) longValue;
		}
	}

	@Override
	public long longValue() {
		if (bigIntegerValue != null) {
			return bigIntegerValue.longValue();
		} else {
			return longValue;
		}
	}
	
	@Override
	public String toString() {
		if (bigIntegerValue != null) {
			return bigIntegerValue.toString();
		} else {
			return String.valueOf(longValue);
		}
	}
	
	public integer_t castToSinteger_t() {
		// TODO implement
		return this;
	}
	
	private static BigInteger toBigInteger(long value) {
		return BigInteger.valueOf(value);
	}	

}
