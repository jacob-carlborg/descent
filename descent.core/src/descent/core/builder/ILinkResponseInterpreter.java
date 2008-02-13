package descent.core.builder;

/**
 * Interprets responses from the linker.
 *
 * @author Robert Fraser
 */
public interface ILinkResponseInterpreter extends IResponseInterpreter
{
	/**
	 * Gets the response of running the linker. Called after all interpet
	 * methods.
	 * 
	 * @return the linker's response
	 */
	public ILinkResponse getLinkResponse();
}
