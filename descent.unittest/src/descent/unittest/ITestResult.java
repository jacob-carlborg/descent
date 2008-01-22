package descent.unittest;

/**
 * Represents the result of running a test, and provides mechanisms for
 * extracting information about it.
 * 
 * In this version, clients should not implement this interface.
 */
public interface ITestResult
{
	/**
	 * The result of running the test.
	 */
	public static enum ResultType
	{
		/**
		 * The test passed.
		 */
		PASSED,
		
		/**
		 * The test failed with an assertion failure. Note that this should
		 * be triggered no matter where the assertion failed (i.e. even if
		 * the assertion failure was outside the test function).
		 */
		FAILED,
		
		/**
		 * The test failed with an unexpected exception.
		 */
		ERROR,
	}
	
	/** 
	 * Gets the result type (passed, failed, or error)
	 * 
	 * @return the result type
	 */
	public ResultType getResultType();
	
	/** 
	 * Gets the file the assertion failed in (for a FAIlED result).
	 * 
	 * @return the file in which the exception was thrown for a FAILED result,
	 *          or null for a PASSED/ERROR result.
	 */
	public String getFile();
	
	/** 
	 * Gets the line the assertion failed at (for a FAIlED result).
	 * 
	 * @return the line on which the exception was thrown for a FAILED result,
	 *         or <b>-1</b> for a PASSED/ERROR result.
	 */
	public int getLine();
	
	/** 
	 * Gets the error message (for a FAILED or ERROR result).
	 * 
	 * @return The exception message. Will always be null for a PASSED result, 
	 *         and may be null if there was no error message specified for a 
	 *         FAILED or ERROR result. */
	public String getMessage();
	
	/** 
	 * Gets the exception type (for an ERROR result).
	 * 
	 * @return the class of the exception for an ERROR result, or null for a 
	 *         PASSED/FAILED result. */
	public String getExceptionType();
	
	/** 
	 * Gets the stack trace of the exception (for a FAILED/ERROR result).
	 * 
	 *  @return The stack trace of the thrown exception, or null for a
	 *          PASSED result. */
	public IStackTraceElement[] getStackTrace();
}
