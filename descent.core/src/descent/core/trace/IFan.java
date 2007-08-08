package descent.core.trace;

/**
 * An invocation of a function.
 */
public interface IFan {
	
	/**
	 * Returns the invoker of the function, or the
	 * function invoked.
	 * @return the the invoker of the function, or the
	 * function invoked
	 */
	ITraceNode getTraceNode();
	
	/**
	 * Returns the number of invocations.
	 * @return the number of invocations
	 */
	long getNumberOfCalls();

}
