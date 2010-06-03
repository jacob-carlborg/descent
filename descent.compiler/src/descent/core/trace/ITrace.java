package descent.core.trace;

/**
 * A profiler trace of a program.
 */
public interface ITrace {
	
	/**
	 * Returns all the traced nodes.
	 * @return the traced nodes
	 */
	ITraceNode[] getNodes();
	
	/**
	 * Returns a traced node given a signature, or
	 * <code>null</code> if the given signature
	 * is not present.
	 * @param signature a signature
	 * @return a traced node given a signature, or
	 * <code>null</code> if the given signature
	 * is not present.
	 */
	ITraceNode getNode(String signature);
	
	/**
	 * Returns the ticks per second of this trace.
	 * @return the ticks per second
	 */
	long getTicksPerSecond();

}
