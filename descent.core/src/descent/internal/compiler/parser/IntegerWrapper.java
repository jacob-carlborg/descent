package descent.internal.compiler.parser;

import java.math.BigInteger;

/**
 * Class to hide wether a number is represented with a long
 * or a BigInteger.
 */
public class IntegerWrapper extends Number {
	
	public final static IntegerWrapper ZERO = new IntegerWrapper(0);
	public final static IntegerWrapper ONE = new IntegerWrapper(1);
	
	private static final long serialVersionUID = 1L;
	
	private long longValue;
	private BigInteger bigIntegerValue;
	
	public IntegerWrapper(long longValue) {
		this.longValue = longValue;
	}
	
	public IntegerWrapper(BigInteger bigIntegerValue) {
		this.bigIntegerValue = bigIntegerValue;
	}
	
	public IntegerWrapper and(BigInteger value) {
		if (bigIntegerValue != null) {
			return new IntegerWrapper(bigIntegerValue.and(value));
		} else {
			return new IntegerWrapper(longValue & value.longValue());
		}
	}
	
	public boolean equals(BigInteger value) {
		if (bigIntegerValue != null) {
			return bigIntegerValue.equals(value);
		} else {
			return longValue == value.longValue();
		}
	}
	
	public int compareTo(BigInteger value) {
		if (bigIntegerValue != null) {
			return bigIntegerValue.compareTo(value);
		} else {
			long otherLong = value.longValue();
			return longValue > otherLong ? 1 : (longValue < otherLong ? - 1 : 0);
		}
	}
	
	public int compareTo(IntegerWrapper value) {
		// TODO can be optimized
		if (bigIntegerValue != null) {			
			return bigIntegerValue.compareTo(value.bigIntegerValue());
		} else {
			long otherLong = value.longValue();
			return longValue > otherLong ? 1 : (longValue < otherLong ? - 1 : 0);
		}
	}
	
	public IntegerWrapper add(int value) {
		if (bigIntegerValue != null) {			
			return new IntegerWrapper(bigIntegerValue.add(new BigInteger(String.valueOf(value))));
		} else {
			if (longValue > 0 && longValue + value < 0) {
				BigInteger bin = new BigInteger(String.valueOf(longValue));
				return new IntegerWrapper(bin.add(new BigInteger(String.valueOf(value))));
			} else {
				return new IntegerWrapper(longValue + value);
			}
		}
	}
	
	public IntegerWrapper add(BigInteger value) {
		if (bigIntegerValue != null) {			
			return new IntegerWrapper(bigIntegerValue.add(value));
		} else {
			BigInteger bin = new BigInteger(String.valueOf(longValue));
			return new IntegerWrapper(bin.add(new BigInteger(String.valueOf(value))));
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
	public int intValue() {
		if (bigIntegerValue != null) {
			return bigIntegerValue.intValue();
		} else {
			return (int) longValue;
		}
	}

	@Override
	public double doubleValue() {
		if (bigIntegerValue != null) {
			return bigIntegerValue.doubleValue();
		} else {
			return (double) longValue;
		}
	}

	@Override
	public float floatValue() {
		if (bigIntegerValue != null) {
			return bigIntegerValue.floatValue();
		} else {
			return (float) longValue;
		}
	}
	
	public BigInteger bigIntegerValue() {
		if (bigIntegerValue != null) {
			return bigIntegerValue;
		} else {
			return new BigInteger(String.valueOf(longValue));
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

}
