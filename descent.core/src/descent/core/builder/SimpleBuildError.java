package descent.core.builder;

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
	 * @see descent.core.builder.IBuildError#getMessage()
	 */
	public String getMessage()
	{
		return message;
	}
	
	/* (non-Javadoc)
	 * @see descent.core.builder.IBuildError#getFile()
	 */
	public String getFile()
	{
		return file;
	}
	
	/* (non-Javadoc)
	 * @see descent.core.builder.IBuildError#getLine()
	 */
	public int getLine()
	{
		return line;
	}
	
	
}
