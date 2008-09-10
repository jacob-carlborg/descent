package descent.building.compiler;

/**
 * A class that accepts incoming compiler messages line-by-line after the
 * execution of a compiler. As messages come in, it is the duty of this class
 * to make appropriate callbacks to the {@link IBuildManager} to, for example,
 * report errors or other output.
 * 
 * <b>This class should be implemented by clients providing a compiler interface.</b>
 * <b>Implementations of this class MUST be thread-safe.</b>
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
