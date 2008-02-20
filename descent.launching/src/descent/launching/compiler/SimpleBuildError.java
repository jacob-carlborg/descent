package descent.launching.compiler;

public class SimpleBuildError implements IBuildError
{
	public final String message;
	public final String file;
	public final int line;
	
	public SimpleBuildError(String message)
	{
		this(message, null);
	}
	
	public SimpleBuildError(String message, String file)
	{
		this(message, file, -1);
	}
	
	public SimpleBuildError(String message, String file, int line)
	{
		this.message = message;
		this.file = file;
		this.line = line;
	}
	
	/* (non-Javadoc)
	 * @see descent.launching.compiler.IBuildError#getMessage()
	 */
	public String getMessage()
	{
		return message;
	}
	
	/* (non-Javadoc)
	 * @see descent.launching.compiler.IBuildError#getFile()
	 */
	public String getFile()
	{
		return file;
	}
	
	/* (non-Javadoc)
	 * @see descent.launching.compiler.IBuildError#getLine()
	 */
	public int getLine()
	{
		return line;
	}
	
	
}