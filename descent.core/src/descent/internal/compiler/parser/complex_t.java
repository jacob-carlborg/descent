package descent.internal.compiler.parser;


public class complex_t {
	
	public final static complex_t ZERO = new complex_t(real_t.ZERO, real_t.ZERO);
	
	public real_t r;
	public real_t i;
	
	public complex_t(real_t r, real_t i) {
		this.r = r;
		this.i = i;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof complex_t)) {
			return false;
		}
		
		complex_t c = (complex_t) obj;
		return r.equals(c.r) && i.equals(c.i);
	}
	
	public complex_t negate()
	{
		// TODO semantic
		return null;
	}
	
	public complex_t multiply(complex_t other)
	{
		// TODO semantic
		return null;
	}
	
	public complex_t divide(complex_t other)
	{
		// TODO semantic
		return null;
	}

}
