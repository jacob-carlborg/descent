package descent.core.builder;

/**
 * Interprets responses from the compiler.
 *
 * @author Robert Fraser
 */
public interface ICompileResponseInterpreter extends IResponseInterpreter
{
	/**
	 * Gets the response of running the compiler. Called after all interpet
	 * methods.
	 * 
	 * @return the compiler's response
	 */
	public ICompileResponse getCompileResponse();
}
