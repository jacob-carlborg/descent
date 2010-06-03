package descent.core.trace;

/**
 * A profiled function.
 */
public interface ITraceNode {
	
	/**
	 * Returns the signature of this function.
	 * @return the signature of this function
	 */
	String getSignature();
	
	/**
	 * Returns the demangled name of this node's signature.
	 * @returnthe demangled name of this node's signature
	 */
	String getDemangledName();
	
	/**
	 * Returns the number of ticks spent in this function,
	 * <i>excluding</i> the number of ticks spent in calls
	 * made by this function.
	 * @returnthe number of ticks spent exclusively in this function
	 */
	long getTicks();
	
	/**
	 * Returns the number of ticks spent in this function,
	 * <i>including</i> the number of ticks spent in calls
	 * made by this function.
	 * @returnthe number of ticks spent in this function
	 */
	long getTreeTicks();
	
	/**
	 * Returns the functions that invoked this function.
	 * @return the functions that invoked this function
	 */
	IFan[] getFanIn();
	
	/**
	 * Returns the functions that this function invoked.
	 * @return the functions that this function invoked
	 */
	IFan[] getFanOut();
	
	/**
	 * Returns the number of times this function was called.
	 * @return the number of times this function was called
	 */
	long getNumberOfCalls();
	
	/**
	 * Returns the time spent in this function in the execution
	 * of the program, in milliseconds.
	 * @return the time spent in this function in the execution
	 * of the program, in milliseconds
	 */
	long getTreeTime();
	
	/**
	 * Returns the time spent in this function in the execution
	 * of the program, in milliseconds.
	 * @return the time spent in this function in the execution
	 * of the program, in milliseconds
	 */
	long getFunctionTime();
	
	/**
	 * Returns the time spent in this function per each call, in milliseconds.
	 * @return the time spent in this function per each call, in milliseconds
	 */
	long getFunctionTimePerCall();

}
