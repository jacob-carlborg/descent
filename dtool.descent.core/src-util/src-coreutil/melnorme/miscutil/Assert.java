/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   IBM Corporation - initial API and implementation
 *     Bruno Medeiros - next implementation
 *******************************************************************************/
package melnorme.miscutil;


/**
 * <code>Assert</code> is util code for contract checking (assertions)
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

        public AssertionFailedException(String message) {
            super(message);
        }
        
        @Override
        public String toString() {
            String message = getLocalizedMessage();
            return "AssertionFailedException" + ((message != null) ? (": " + message) : "");
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
			throw new AssertionFailedException(message); //$NON-NLS-1$
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
		return Assert.isTrue(expression, null); //$NON-NLS-1$
	}
	/** Like {@link #isTrue(boolean, String)} with empty message */
	public static boolean assertTrue(boolean expression) {
		return Assert.isTrue(expression, null); //$NON-NLS-1$
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
		Assert.isTrue(!(object == null), null);
	}
	/** Like {@link #isNotNull(Object, String)} with empty message.	 */
	public static void assertNotNull(Object object) {
		Assert.isTrue(!(object == null), null);
	}
	
	
    /** Asserts that the given object is <code>null</code>. */
	public static void isNull(Object object) {
		Assert.isTrue(object == null);
	}
	/** Asserts that the given object is <code>null</code>. */
	public static void assertIsNull(Object object) {
		Assert.isTrue(object == null);
	}
	
    /** Asserts that given object1 equals object2. */
	public static void equals(Object object1, Object object2) {
		Assert.isTrue(object1.equals(object2));
	}
    /** Asserts that given object1 equals object2. */
	public static void assertEquals(Object object1, Object object2) {
		Assert.isTrue(object1.equals(object2));
	}

	
	/** Causes an inconditional assertion failure, with message msg. 
	 * Never returns.
	 */
	public static AssertionFailedException fail(String msg) {
		throw new AssertionFailedException(msg);
	}
	/** Causes an inconditional assertion failure, with message msg. 
	 * Never returns.
	 */
	public static AssertionFailedException assertFail(String msg) {
		return Assert.fail(msg);
	}
	
	
	/** Like {@link #fail(String)} with empty message. */
	public static AssertionFailedException fail() {
		throw new AssertionFailedException("fail.");
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