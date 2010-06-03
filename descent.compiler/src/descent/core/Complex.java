package descent.core;

import descent.core.dom.Expression;

/**
 * Represents a complex value.
 * @see ICodeAssist#codeEvaluate(int)
 * @see ICodeAssist#codeEvaluate(int, descent.core.WorkingCopyOwner)
 * @see Expression#resolveConstantExpressionValue()
 */
public class Complex {
	
	private Number realPart;
	private Number imaginaryPart;
	
	/**
	 * Constructs a new complex value with real and imaginary parts.
	 * @param realPart the real part
	 * @param imaginaryPart the imaginary part
	 */
	public Complex(Number realPart, Number imaginaryPart) {
		this.realPart = realPart;
		this.imaginaryPart = imaginaryPart;		
	}
	
	/**
	 * Returns the real part.
	 * @return the real part
	 */
	public Number getRealPart() {
		return realPart;
	}
	
	/**
	 * Returns the imaginary part
	 * @return the imaginary part
	 */
	public Number getImaginaryPart() {
		return imaginaryPart;
	}
	
	@Override
	public String toString() {
		return realPart + " + " + imaginaryPart + "i";
	}

}
