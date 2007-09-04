package descent.internal.compiler.parser;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.core.runtime.Assert;

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
	
	public real_t(double nanOrInfinite) {
		Assert.isTrue(Double.isInfinite(nanOrInfinite) || Double.isNaN(nanOrInfinite));
		this.nanOrInfinite = nanOrInfinite;
	}
	
	public integer_t toIntegerWrapper() {
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
			return value.equals(r.value);
		} else {
			return Double.isNaN(nanOrInfinite) == Double.isNaN(r.nanOrInfinite);
		}
	}
	
	public real_t negate()
	{
		return new real_t(value.negate());
	}

}
