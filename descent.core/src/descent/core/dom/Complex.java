package descent.core.dom;

import descent.core.ICodeAssist;

/**
 * Represents a complex value.
 * @see ICodeAssist#codeEvaluate(int)
 * @see ICodeAssist#codeEvaluate(int, descent.core.WorkingCopyOwner)
 * @see Expression#resolveConstantExpressionValue()
 */
public class Complex {
	
	private Object realPart;
	private Object imaginaryPart;
	
	/**
	 * Constructs a new complex value with real and imaginary parts.
	 * @param realPart the real part
	 * @param imaginaryPart the imaginary part
	 */
	public Complex(Object realPart, Object imaginaryPart) {
		this.realPart = realPart;
		this.imaginaryPart = imaginaryPart;		
	}
	
	/**
	 * Returns the real part.
	 * @return the real part
	 */
	public Object getRealPart() {
		return realPart;
	}
	
	/**
	 * Returns the imaginary part
	 * @return the imaginary part
	 */
	public Object getImaginaryPart() {
		return imaginaryPart;
	}
	
	@Override
	public String toString() {
		return realPart + " + " + imaginaryPart + "i";
	}

}
