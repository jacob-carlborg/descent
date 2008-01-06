package descent.core.dom;

import descent.core.ICodeAssist;

/**
 * Represents the void value.
 * @see ICodeAssist#codeEvaluate(int)
 * @see ICodeAssist#codeEvaluate(int, descent.core.WorkingCopyOwner)
 * @see Expression#resolveConstantExpressionValue()
 */
public class Void {
	
	private static Void instance = new Void();
	private Void() { }
	
	/**
	 * Returns the singleton instance pf void.
	 */
	public static Void getInstance() {
		return instance;
	}
	
	@Override
	public String toString() {
		return "void";
	}

}
