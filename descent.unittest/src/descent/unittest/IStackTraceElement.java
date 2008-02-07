package descent.unittest;

/**
 * Represents a single stack frame in a stack trace of a D application.
 * 
 * In this version, clients should not implement this interface.
 */
public interface IStackTraceElement
{
	/** 
	 * Gets the name and signature of the executing function. The format is
	 * undefined.
	 * 
	 * @return the name of the function that was executing.
	 */
	public String getFunction();
	
	/**
	 * Finds out whether file/line information was found for this exception.
	 * If this returns <code>true</code>, {@link #getFile()} and 
	 * {@link #getLine()} should return valid values (non-null and >0,
	 * respectively), and {@link #getAddress()} should return an invalid
	 * value. Otherwise, the opposite should be true.
	 * 
	 * @return <code>true</true> if and only if file and line information was
	 *         found for this stack frame.
	 */
	public boolean lineInfoFound();
	
	/** 
	 * Gets the name of the file on which the executing function is defined.
	 * 
	 * @return the file the function is defined in, or null if only the
	 *         address was found.
	 */
	public String getFile();
	
	/** 
	 * Gets the line that is executing in this stack frame.
	 * 
	 * @return the line that was executing, or -1 if only the address was
	 *         found.
	 */
	public int getLine();
	
	/** 
	 * Gets the executing memory address.
	 * 
	 * @return The address of the executing code or -1 if the file/line was
	 *         found.
	 */
	public long getAddress();
}
