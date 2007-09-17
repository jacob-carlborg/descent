package melnorme.miscutil;


/**
 * (Based on org.eclipse.core.runtime.Assert)
 * 
 * <code>Assert</code> is useful for for embedding runtime sanity checks
 * in code. The static predicate methods all test a condition and throw some
 * type of unchecked exception if the condition does not hold.
 * <p>
 * Assertion failure exceptions, like most runtime exceptions, are
 * thrown when something is misbehaving. Assertion failures are invariably
 * unspecified behavior; consequently, clients should never rely on
 * these being thrown (or not thrown). <b>If you find yourself in the
 * position where you need to catch an assertion failure, you have most 
 * certainly written your program incorrectly.</b>
 * </p>
 */
public abstract class Assert {

    /* This class is not intended to be instantiated. */
    
    /** <code>AssertionFailedException</code> is a runtime exception thrown by
	 * some of the methods in <code>Assert</code>.
	 * <p>
	 * This class is not declared public to prevent some misuses; programs that
	 * catch or otherwise depend on assertion failures are susceptible to
	 * unexpected breakage when assertions in the code are added or removed.
	 * </p>
	 */
    private static class AssertionFailedException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public AssertionFailedException() { }

        public AssertionFailedException(String detail) {
            super(detail);
        }
    }

    /** Asserts that the given object is <code>null</code>. */
	public static void isNull(Object obj) {
		Assert.isTrue(obj == null);
	}

	/** Asserts that the given object is not <code>null</code>. If this
	 * is not the case, some kind of unchecked exception is thrown.
	 * The given message is included in that exception, to aid debugging.
	 */
	public static void isNotNull(Object object, String message) {
		isTrue(!(object == null), message);
	}
	
	/** Like {@link #isNotNull(Object, String)} with empty message.	 */
	public static void isNotNull(Object object) {
		isTrue(!(object == null), "");
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
			throw new AssertionFailedException("assertion failed: " + message); //$NON-NLS-1$
		}
		return expression;
	}
	
	/** Like {@link #isTrue(boolean, String)} with empty message
	 */
	public static boolean isTrue(boolean expression) {
		return isTrue(expression, ""); //$NON-NLS-1$
	}

	
	/** Causes an inconditional assertion failure, with message msg.
	 */
	public static void fail(String msg) {
		throw new AssertionFailedException("ASSERT FAIL:" + msg);
	}
	
	/** Like {@link #fail(String)} with empty message. 
	 */
	public static void fail() {
		throw new AssertionFailedException("ASSERT FAIL");
	}

	@Deprecated
	public static void failTODO() {
		fail();
	}

	/* ============================================================= */

	/** Asserts that the given object is <code>null</code>. */
	public static void assertIsNull(Object obj) {
		Assert.isTrue(obj == null);
	}

	/** Asserts that the given object is not <code>null</code>. If this
	 * is not the case, some kind of unchecked exception is thrown.
	 * The given message is included in that exception, to aid debugging.
	 */
	public static void assertNotNull(Object object, String message) {
		Assert.isTrue(!(object == null), message);
	}
	
	/** Like {@link #isNotNull(Object, String)} with empty message.	 */
	public static void assertNotNull(Object object) {
		Assert.isTrue(!(object == null), "");
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
	public static boolean assertTrue(boolean expression) {
		return Assert.isTrue(expression, ""); //$NON-NLS-1$
	}

	
	/** Causes an inconditional assertion failure, with message msg.
	 */
	public static void assertFail(String msg) {
		Assert.fail(msg);
	}
	
	/** Like {@link #fail(String)} with empty message. 
	 */
	public static void assertFail() {
		Assert.fail();
	}
	
}
