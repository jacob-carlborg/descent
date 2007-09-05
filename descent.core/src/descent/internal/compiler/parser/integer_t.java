package descent.internal.compiler.parser;

import java.math.BigInteger;

/**
 * Class to hide wether a number is represented with a long
 * or a BigInteger.
 */
public class integer_t extends Number {
	
	public final static integer_t ZERO = new integer_t(0);
	public final static integer_t ONE = new integer_t(1);
	
	private static final long serialVersionUID = 1L;
	
	private int intValue;
	private BigInteger bigIntegerValue;
	
	public integer_t(int intValue) {
		this.intValue = intValue;
	}
	
	public integer_t(BigInteger bigIntegerValue) {
		this.bigIntegerValue = bigIntegerValue;
	}
	
	public integer_t and(BigInteger value) {
		if (bigIntegerValue == null) {
			bigIntegerValue = BigInteger.valueOf(intValue);
		}
		return new integer_t(bigIntegerValue.and(value));
	}
	
	public boolean equals(Object other) {
		if (other instanceof BigInteger) {
			return equals((BigInteger) other);
		} else if (other instanceof integer_t) {
			return equals((integer_t) other);
		} else {
			return false;
		}
	}
	
	public boolean equals(int other) {
		if (bigIntegerValue == null) {
			return intValue == other;
		} else {
			return bigIntegerValue.equals(new BigInteger(String.valueOf(other)));
		}
	}
	
	public boolean equals(BigInteger other) {
		if (bigIntegerValue == null) {
			bigIntegerValue = BigInteger.valueOf(intValue);
		}
		return bigIntegerValue.equals(other);
	}
	
	public boolean equals(integer_t other) {
		if (bigIntegerValue == null) {
			if (other.bigIntegerValue == null) {
				return intValue == other.intValue;
			} else {
				bigIntegerValue = BigInteger.valueOf(intValue);
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
	
	public int compareTo(int value) {
		if (bigIntegerValue == null) {
			return intValue > value ? 1 : (intValue < value ? -1 : 0);
		} else {
			return bigIntegerValue.compareTo(BigInteger.valueOf(intValue));
		}
	}
	
	public int compareTo(BigInteger value) {
		if (bigIntegerValue == null) {
			bigIntegerValue = BigInteger.valueOf(intValue);
		}
		
		return bigIntegerValue.compareTo(value);
	}
	
	public int compareTo(integer_t value) {
		if (bigIntegerValue != null) {
			return bigIntegerValue.compareTo(value.bigIntegerValue());
		} else {
			if (value.bigIntegerValue == null) {
				long otherLong = value.longValue();
				return intValue > otherLong ? 1 : (intValue < otherLong ? - 1 : 0);
			} else {
				bigIntegerValue = BigInteger.valueOf(intValue);
				value.bigIntegerValue = BigInteger.valueOf(intValue);
				return bigIntegerValue.compareTo(value.bigIntegerValue);
			}
		}
	}
	
	public integer_t add(int value) {
		if (bigIntegerValue != null) {			
			return new integer_t(bigIntegerValue.add(new BigInteger(String.valueOf(value))));
		} else {
			if (intValue > 0 && intValue + value < 0) {
				BigInteger bin = BigInteger.valueOf(intValue);
				return new integer_t(bin.add(BigInteger.valueOf(intValue)));
			} else {
				return new integer_t(intValue + value);
			}
		}
	}
	
	public integer_t add(BigInteger value) {
		if (bigIntegerValue != null) {			
			return new integer_t(bigIntegerValue.add(value));
		} else {
			BigInteger bin = BigInteger.valueOf(intValue);
			return new integer_t(bin.add(BigInteger.valueOf(intValue)));
		}
	}
	
	public integer_t add(integer_t value)
	{
		if (bigIntegerValue != null) {
			return value.bigIntegerValue != null ?
					new integer_t(bigIntegerValue.add(value.bigIntegerValue)) :
					new integer_t(bigIntegerValue.add(BigInteger.valueOf(value.intValue)));
		} else {
			BigInteger bin = BigInteger.valueOf(intValue);
			return value.bigIntegerValue != null ?
					new integer_t(bin.add(value.bigIntegerValue)) :
					new integer_t(bin.add(BigInteger.valueOf(value.intValue)));
		}
	}
	
	public integer_t subtract(integer_t value)
	{
		if (bigIntegerValue != null) {
			return value.bigIntegerValue != null ?
					new integer_t(bigIntegerValue.subtract(value.bigIntegerValue)) :
					new integer_t(bigIntegerValue.subtract(BigInteger.valueOf(value.intValue)));
		} else {
			BigInteger bin = BigInteger.valueOf(intValue);
			return value.bigIntegerValue != null ?
					new integer_t(bin.subtract(value.bigIntegerValue)) :
					new integer_t(bin.subtract(BigInteger.valueOf(value.intValue)));
		}
	}
	
	public integer_t multiply(integer_t value)
	{
		if (bigIntegerValue != null) {
			return value.bigIntegerValue != null ?
					new integer_t(bigIntegerValue.multiply(value.bigIntegerValue)) :
					new integer_t(bigIntegerValue.multiply(BigInteger.valueOf(value.intValue)));
		} else {
			BigInteger bin = BigInteger.valueOf(intValue);
			return value.bigIntegerValue != null ?
					new integer_t(bin.multiply(value.bigIntegerValue)) :
					new integer_t(bin.multiply(BigInteger.valueOf(value.intValue)));
		}
	}
	
	public integer_t divide(integer_t value)
	{
		if (bigIntegerValue != null) {
			return value.bigIntegerValue != null ?
					new integer_t(bigIntegerValue.divide(value.bigIntegerValue)) :
					new integer_t(bigIntegerValue.divide(BigInteger.valueOf(value.intValue)));
		} else {
			BigInteger bin = BigInteger.valueOf(intValue);
			return value.bigIntegerValue != null ?
					new integer_t(bin.divide(value.bigIntegerValue)) :
					new integer_t(bin.divide(BigInteger.valueOf(value.intValue)));
		}
	}
	
	public integer_t remainder(integer_t value)
	{
		if (bigIntegerValue != null) {
			return value.bigIntegerValue != null ?
					new integer_t(bigIntegerValue.remainder(value.bigIntegerValue)) :
					new integer_t(bigIntegerValue.remainder(BigInteger.valueOf(value.intValue)));
		} else {
			BigInteger bin = BigInteger.valueOf(intValue);
			return value.bigIntegerValue != null ?
					new integer_t(bin.remainder(value.bigIntegerValue)) :
					new integer_t(bin.remainder(BigInteger.valueOf(value.intValue)));
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
			return BigInteger.valueOf(intValue);
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

	public integer_t times(int value) {
		if (bigIntegerValue == null) {
			bigIntegerValue = BigInteger.valueOf(intValue);
		}
		
		return new integer_t(bigIntegerValue.add(new BigInteger(String.valueOf(value))));
	}
	
	public integer_t negate()
	{
		if(null != bigIntegerValue)
			return new integer_t(bigIntegerValue.negate());
		else
			return new integer_t(-intValue);
	}
	
	public integer_t complement()
	{
		if(null != bigIntegerValue)
			return new integer_t(bigIntegerValue.not());
		else
			return new integer_t(~intValue);
	}
	
	public integer_t and(integer_t value)
	{
		if (bigIntegerValue != null) {
			return value.bigIntegerValue != null ?
					new integer_t(bigIntegerValue.and(value.bigIntegerValue)) :
					new integer_t(bigIntegerValue.and(BigInteger.valueOf(value.intValue)));
		} else {
			BigInteger bin = BigInteger.valueOf(intValue);
			return value.bigIntegerValue != null ?
					new integer_t(bin.and(value.bigIntegerValue)) :
					new integer_t(bin.and(BigInteger.valueOf(value.intValue)));
		}
	}
	
	public integer_t or(integer_t value)
	{
		if (bigIntegerValue != null) {
			return value.bigIntegerValue != null ?
					new integer_t(bigIntegerValue.or(value.bigIntegerValue)) :
					new integer_t(bigIntegerValue.or(BigInteger.valueOf(value.intValue)));
		} else {
			BigInteger bin = BigInteger.valueOf(intValue);
			return value.bigIntegerValue != null ?
					new integer_t(bin.or(value.bigIntegerValue)) :
					new integer_t(bin.or(BigInteger.valueOf(value.intValue)));
		}
	}
	
	public integer_t xor(integer_t value)
	{
		if (bigIntegerValue != null) {
			return value.bigIntegerValue != null ?
					new integer_t(bigIntegerValue.xor(value.bigIntegerValue)) :
					new integer_t(bigIntegerValue.xor(BigInteger.valueOf(value.intValue)));
		} else {
			BigInteger bin = BigInteger.valueOf(intValue);
			return value.bigIntegerValue != null ?
					new integer_t(bin.xor(value.bigIntegerValue)) :
					new integer_t(bin.xor(BigInteger.valueOf(value.intValue)));
		}
	}
	
	public integer_t shiftLeft(integer_t value)
	{
		if (bigIntegerValue != null) {
			return value.bigIntegerValue != null ?
					new integer_t(bigIntegerValue.shiftLeft(value.bigIntegerValue.intValue())) :
					new integer_t(bigIntegerValue.shiftLeft(value.intValue));
		} else {
			BigInteger bin = BigInteger.valueOf(intValue);
			return value.bigIntegerValue != null ?
					new integer_t(bin.shiftLeft(value.bigIntegerValue.intValue())) :
					new integer_t(bin.shiftLeft(value.intValue));
		}
	}
	
	public integer_t shiftRight(integer_t value)
	{
		if (bigIntegerValue != null) {
			return value.bigIntegerValue != null ?
					new integer_t(bigIntegerValue.shiftRight(value.bigIntegerValue.intValue())) :
					new integer_t(bigIntegerValue.shiftRight(value.intValue));
		} else {
			BigInteger bin = BigInteger.valueOf(intValue);
			return value.bigIntegerValue != null ?
					new integer_t(bin.shiftRight(value.bigIntegerValue.intValue())) :
					new integer_t(bin.shiftRight(value.intValue));
		}
	}

}
