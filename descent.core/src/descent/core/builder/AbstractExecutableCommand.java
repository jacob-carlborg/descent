package descent.core.builder;

public abstract class AbstractExecutableCommand implements IExecutableCommand
{
	protected String executableFile;
	
	public void setDefaults()
	{
		executableFile = null;
	}
	
	public boolean isValid()
	{
		return executableFile != null;
	}
	
	/* (non-Javadoc)
	 * @see descent.core.builder.IExecutableCommand#getExecutableName()
	 */
	public String getExecutableFile()
	{
		return executableFile;
	}

	/* (non-Javadoc)
	 * @see descent.core.builder.IExecutableCommand#setExecutableName(java.lang.String)
	 */
	public void setExecutableFile(String executableFile)
	{
		this.executableFile = executableFile;
	}
}
