package descent.internal.compiler.parser;


public class Complex {
	
	public final static Complex ZERO = new Complex(Real.ZERO, Real.ZERO);
	
	public Real r;
	public Real i;
	
	public Complex(Real r, Real i) {
		this.r = r;
		this.i = i;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Complex)) {
			return false;
		}
		
		Complex c = (Complex) obj;
		return r.equals(c.r) && i.equals(c.i);
	}

}
