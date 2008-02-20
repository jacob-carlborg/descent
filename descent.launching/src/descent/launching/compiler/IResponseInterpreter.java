package descent.launching.compiler;

/**
 * A class that accepts incoming compiler messages line-by-line after the
 * execution of a compiler and returns results about them after the compiler
 * has completd execution. 
 * 
 * The builder will create a single response interpreter per execution of the
 * compiler or linker, which should process each line rcieved in its 
 * {@link #interpret(String)} method. {@link #getResponse()} should be used
 * to get the response, and is garunteed to be called after all input is sent.
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
	
	/**
	 * Gets the result of the build command
	 */
	public BuildResponse getResponse();
}
