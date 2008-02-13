package descent.core.builder;

/**
 * A class that accepts incoming compiler messages line-by-line after the
 * execution of a compiler and returns results about them after the compiler
 * has completd execution. 
 * 
 * The builder will create a single response interpreter per execution of the
 * compiler or linker, which should process each line rcieved in its 
 * {@link #interpret(String)} method. Any subinterface methods to get output
 * are garunteed to be called after all interpret calls.
 * 
 * @author Robert Fraser
 */
public interface IResponseInterpreter
{
	/**
	 * Called when a new line is sent to stdout of the monitored process.
	 * 
	 * @param line the line that was sent to stdout
	 */
	public void interpret(String line);
	
	/**
	 * Called when a new line is sent to the stderr of the monitored process.
	 * 
	 * @param line the line that was sent to stderr
	 */
	public void interpretError(String line);
	
}
