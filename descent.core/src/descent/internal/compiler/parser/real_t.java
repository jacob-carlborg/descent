package descent.internal.compiler.parser;

import java.math.BigDecimal;
import java.math.BigInteger;

// DMD 1.020
public class real_t {

	public final static real_t ZERO = new real_t(BigDecimal.ZERO);

	public BigDecimal value;
	public double nanOrInfinite;

	public real_t(BigInteger value) {
		this.value = new BigDecimal(value);
	}

	public real_t(integer_t value) {
		this.value = new BigDecimal(value.bigIntegerValue());
	}

	public real_t(BigDecimal value) {
		this.value = value;
	}

	public real_t(double doubleVal) {
		if (Double.isInfinite(doubleVal) || Double.isNaN(doubleVal))
			nanOrInfinite = doubleVal;
		else
			value = new BigDecimal(doubleVal);
	}

	public integer_t to_integer_t() {
		return new integer_t(value.toBigInteger());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof real_t)) {
			return false;
		}

		real_t r = (real_t) obj;
		if ((value != null) != (r.value != null)) {
			return false;
		}

		if (value != null) {
			// Read documentation of BigDecimal.equals: it compares
			// also the scale :-(
			return value.compareTo(r.value) == 0;
		} else {
			return Double.isNaN(nanOrInfinite) == Double.isNaN(r.nanOrInfinite);
		}
	}

	// FIXME Figure out the semantics with infinite and NaN values

	public real_t negate() {
		return new real_t(value.negate());
	}

	public real_t add(real_t other) {
		return new real_t(value.add(other.value));
	}

	public real_t subtract(real_t other) {
		return new real_t(value.subtract(other.value));
	}

	public real_t multiply(real_t other) {
		return new real_t(value.multiply(other.value));
	}

	public real_t divide(real_t other) {
		return new real_t(value.divide(other.value));
	}

	public real_t remainder(real_t other) {
		return new real_t(value.remainder(other.value));
	}

	public int compareTo(real_t other) {
		if (isnan() || other.isnan()) {
			return -1;
		}
		return value.compareTo(other.value);
	}

	public boolean isnan() {
		return value == null && Double.isNaN(nanOrInfinite);
	}
	
	public double doubleValue() {
		return value.doubleValue();
	}

	@Override
	public String toString() {
		// TODO how is NaN or infinite represented in D string buffers?
		return value.toString();
	}

}
