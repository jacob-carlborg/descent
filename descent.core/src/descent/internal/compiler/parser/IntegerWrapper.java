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
	
	private int intValue;
	private BigInteger bigIntegerValue;
	
	public IntegerWrapper(int intValue) {
		this.intValue = intValue;
	}
	
	public IntegerWrapper(BigInteger bigIntegerValue) {
		this.bigIntegerValue = bigIntegerValue;
	}
	
	public IntegerWrapper and(BigInteger value) {
		if (bigIntegerValue == null) {
			bigIntegerValue = new BigInteger(String.valueOf(intValue));
		}
		return new IntegerWrapper(bigIntegerValue.and(value));
	}
	
	public boolean equals(Object other) {
		if (other instanceof BigInteger) {
			return equals((BigInteger) other);
		} else if (other instanceof IntegerWrapper) {
			return equals((IntegerWrapper) other);
		} else {
			return false;
		}
	}
	
	public boolean equals(BigInteger other) {
		if (bigIntegerValue != null) {
			return bigIntegerValue.equals(other);
		} else {
			return intValue == other.longValue();
		}
	}
	
	public boolean equals(IntegerWrapper other) {
		if (bigIntegerValue == null) {
			if (other.bigIntegerValue == null) {
				return intValue == other.intValue;
			} else {
				bigIntegerValue = new BigInteger(String.valueOf(intValue));
				return bigIntegerValue.equals(other.bigIntegerValue);
			}
		} else {
			if (other.bigIntegerValue == null) {
				other.bigIntegerValue = new BigInteger(String.valueOf(other.bigIntegerValue));
				return bigIntegerValue.equals(other.bigIntegerValue);
			} else {
				return bigIntegerValue.equals(other.bigIntegerValue);
			}
		}
	}
	
	public int compareTo(BigInteger value) {
		if (bigIntegerValue == null) {
			bigIntegerValue = new BigInteger(String.valueOf(intValue));
		}
		
		return bigIntegerValue.compareTo(value);
	}
	
	public int compareTo(IntegerWrapper value) {
		if (bigIntegerValue != null) {
			return bigIntegerValue.compareTo(value.bigIntegerValue());
		} else {
			if (value.bigIntegerValue == null) {
				long otherLong = value.longValue();
				return intValue > otherLong ? 1 : (intValue < otherLong ? - 1 : 0);
			} else {
				bigIntegerValue = new BigInteger(String.valueOf(intValue));
				value.bigIntegerValue = new BigInteger(String.valueOf(value.intValue));
				return bigIntegerValue.compareTo(value.bigIntegerValue);
			}
		}
	}
	
	public IntegerWrapper add(int value) {
		if (bigIntegerValue != null) {			
			return new IntegerWrapper(bigIntegerValue.add(new BigInteger(String.valueOf(value))));
		} else {
			if (intValue > 0 && intValue + value < 0) {
				BigInteger bin = new BigInteger(String.valueOf(intValue));
				return new IntegerWrapper(bin.add(new BigInteger(String.valueOf(value))));
			} else {
				return new IntegerWrapper(intValue + value);
			}
		}
	}
	
	public IntegerWrapper add(BigInteger value) {
		if (bigIntegerValue != null) {			
			return new IntegerWrapper(bigIntegerValue.add(value));
		} else {
			BigInteger bin = new BigInteger(String.valueOf(intValue));
			return new IntegerWrapper(bin.add(new BigInteger(String.valueOf(value))));
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
	public int intValue() {
		if (bigIntegerValue != null) {
			return bigIntegerValue.intValue();
		} else {
			return (int) intValue;
		}
	}

	@Override
	public double doubleValue() {
		if (bigIntegerValue != null) {
			return bigIntegerValue.doubleValue();
		} else {
			return (double) intValue;
		}
	}

	@Override
	public float floatValue() {
		if (bigIntegerValue != null) {
			return bigIntegerValue.floatValue();
		} else {
			return (float) intValue;
		}
	}
	
	public BigInteger bigIntegerValue() {
		if (bigIntegerValue != null) {
			return bigIntegerValue;
		} else {
			return new BigInteger(String.valueOf(intValue));
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

}
