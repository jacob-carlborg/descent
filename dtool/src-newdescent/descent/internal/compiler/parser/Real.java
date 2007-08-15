package descent.internal.compiler.parser;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.core.runtime.Assert;

public class Real {
	
	public final static Real ZERO = new Real(BigDecimal.ZERO);
	
	public BigDecimal value;
	public double nanOrInfinite;
	
	public Real(BigInteger value) {
		this.value = new BigDecimal(value);
	}
	
	public Real(BigDecimal value) {
		this.value = value;
	}
	
	public Real(double nanOrInfinite) {
		Assert.isTrue(Double.isInfinite(nanOrInfinite) || Double.isNaN(nanOrInfinite));
		this.nanOrInfinite = nanOrInfinite;
	}
	
	public BigInteger toBigInteger() {
		return value.toBigInteger();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Real)) {
			return false;
		}
		
		Real r = (Real) obj;
		if ((value != null) != (r.value != null)) {
			return false;
		}
		
		if (value != null) {
			return value.equals(r.value);
		} else {
			return Double.isNaN(nanOrInfinite) == Double.isNaN(r.nanOrInfinite);
		}
	}
	

}
