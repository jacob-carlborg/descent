package melnorme.miscutil;


/**
 * <p>
 * (Somewhat based on org.eclipse.core.runtime.Assert)
 * </p>
 * 
 * <code>Assert</code> is useful for for embedding runtime sanity checks in
 * code. The static predicate methods all test a condition and throw some type
 * of unchecked exception if the condition does not hold.
 * <p>
 * Assertion failure exceptions, like most runtime exceptions, are thrown when
 * something is misbehaving. Assertion failures are invariably unspecified
 * behavior; consequently, clients should never rely on these being thrown (or
 * not thrown). <b>If you find yourself in the position where you need to catch
 * an assertion failure, you have most certainly written your program
 * incorrectly.</b>
 * </p>
 * <p>
 * For each method in this class, such as 'isTrue', there is an identical method
 * with the 'assert' prefix, (ie, assertTrue). This is a convenience for the use of
 * Java's static imports.
 * </p>
 */
/* This class is not intended to be instantiated. */
public abstract class Assert {

    
    /** <code>AssertionFailedException</code> is a runtime exception thrown by
	 * some of the methods in <code>Assert</code>.
	 * <p>
	 * This class is not declared public to prevent some misuses; programs that
	 * catch or otherwise depend on assertion failures are susceptible to
	 * unexpected breakage when assertions in the code are added or removed.
	 * </p>
	 */
    @SuppressWarnings("serial")
	private static class AssertionFailedException extends RuntimeException {
    	// Note: it is quite useful to place a class creation breakpoint in this class.

        public AssertionFailedException() { 
        }

        public AssertionFailedException(String detail) {
            super(detail);
        }
        
    }
    
	/** Asserts that the given boolean is <code>true</code>. If this
	 * is not the case, some kind of unchecked exception is thrown.
	 * The given message is included in that exception, to aid debugging.
	 *
	 * @return <code>true</code> if the check passes (does not return
	 *    if the check fails)
	 */
	public static boolean isTrue(boolean expression, String message) {
		if (!expression) {
			expression = false;  // dummy statement to allow breakpoint placement
			throw new AssertionFailedException("Assertion failed: " + message); //$NON-NLS-1$
		}
		return expression;
	}
	
	/** Asserts that the given boolean is <code>true</code>. If this
	 * is not the case, some kind of unchecked exception is thrown.
	 * The given message is included in that exception, to aid debugging.
	 *
	 * @return <code>true</code> if the check passes (does not return
	 *    if the check fails)
	 */
	public static boolean assertTrue(boolean expression, String message) {
		return Assert.isTrue(expression, message);
	}
	
	/** Like {@link #isTrue(boolean, String)} with empty message
	 */
	public static boolean isTrue(boolean expression) {
		return Assert.isTrue(expression, ""); //$NON-NLS-1$
	}
	/** Like {@link #isTrue(boolean, String)} with empty message */
	public static boolean assertTrue(boolean expression) {
		return Assert.isTrue(expression, ""); //$NON-NLS-1$
	}
	
	
	/** Asserts that the given object is not <code>null</code>. If this
	 * is not the case, some kind of unchecked exception is thrown.
	 * The given message is included in that exception, to aid debugging.
	 */
	public static void isNotNull(Object object, String message) {
		Assert.isTrue(!(object == null), message);
	}
	/** Asserts that the given object is not <code>null</code>. If this
	 * is not the case, some kind of unchecked exception is thrown.
	 * The given message is included in that exception, to aid debugging.
	 */
	public static void assertNotNull(Object object, String message) {
		Assert.isTrue(!(object == null), message);
	}
	
	/** Like {@link #isNotNull(Object, String)} with empty message.	 */
	public static void isNotNull(Object object) {
		Assert.isTrue(!(object == null), "");
	}
	/** Like {@link #isNotNull(Object, String)} with empty message.	 */
	public static void assertNotNull(Object object) {
		Assert.isTrue(!(object == null), "");
	}
	
	
    /** Asserts that the given object is <code>null</code>. */
	public static void isNull(Object object) {
		Assert.isTrue(object == null);
	}
	/** Asserts that the given object is <code>null</code>. */
	public static void assertIsNull(Object object) {
		Assert.isTrue(object == null);
	}

	
	/** Causes an inconditional assertion failure, with message msg. 
	 * Never returns.
	 */
	public static AssertionFailedException fail(String msg) {
		throw new AssertionFailedException("Assert fail: " + msg);
	}
	/** Causes an inconditional assertion failure, with message msg. 
	 * Never returns.
	 */
	public static AssertionFailedException assertFail(String msg) {
		return Assert.fail(msg);
	}
	
	
	/** Like {@link #fail(String)} with empty message. */
	public static AssertionFailedException fail() {
		throw new AssertionFailedException("Assert fail.");
	}
	/** Like {@link #fail(String)} with empty message. */
	public static AssertionFailedException assertFail() {
		return Assert.fail();
	}
	
	
	/** Like {@link #fail()}, specifically signals unreachable code */
	public static AssertionFailedException unreachable() {
		return Assert.fail("Unreachable code.");
	}
	/** Like {@link #fail()}, specifically signals unreachable code */
	public static AssertionFailedException assertUnreachable() {
		throw Assert.unreachable();
	}

	
	/** Causes an Assert.failt signaling a feature that is not yet implemented. 
	 * Uses the Deprecated annotation solely to cause a warning. */
	@Deprecated
	public static void failTODO() {
		Assert.fail("TODO");
	}
	/** Causes an Assert.failt signaling a feature that is not yet implemented. 
	 * Uses the Deprecated annotation solely to cause a warning. */
	@Deprecated
	public static void assertFailTODO() {
		Assert.fail("TODO");
	}

}
