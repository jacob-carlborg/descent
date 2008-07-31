package melnorme.miscutil;

/**
 * Same as {@link Assert}, but used for function pre-conditions. It throws
 * {@link IllegalArgumentException} instead of some other assertion error
 * but it is still incorrect to rely this throwing.
 */
public final class AssertIn {

    /** This class is not intended to be instantiated. */
    private AssertIn() {
    }
    
	/** Like {@link Assert#isNotNull(Object, String)} 
	 * but throws IllegalArgumentException instead.
	 */
	public static void isNotNull(Object object, String message) {
		if (object == null)
			throw new IllegalArgumentException("null argument:" + message); //$NON-NLS-1$
	}
	
	/** Like {@link #isNotNull(Object object, String message)} , 
	 * with empty message.
	 */
	public static void isNotNull(Object object) {
		isNotNull(object, ""); //$NON-NLS-1$
	}


	/** Like {@link Assert#isTrue(Object, String)} but throws 
	 * IllegalArgumentException instead.
	 */
	public static boolean isTrue(boolean expression, String message) {
		if (!expression)
			throw new IllegalArgumentException("assertion failed: " + message); //$NON-NLS-1$
		return expression;
	}
	
	/** Like {@link #isTrue(boolean, String)} with empty message
	 */
	public static boolean isTrue(boolean expression) {
		return isTrue(expression, ""); //$NON-NLS-1$
	}


	
	/** Causes an inconditional assertion failure, with message msg.
	 */
	/*public static void fail(String msg) {
		throw new IllegalArgumentException("ASSERT FALSE:" + msg);
	}*/
	
	/** Like {@link #fail(String)} with empty message. 
	 */
	/*public static void fail() {
		throw new IllegalArgumentException("ASSERT FALSE");
	}*/

}
