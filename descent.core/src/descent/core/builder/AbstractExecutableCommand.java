package descent.core.builder;

import java.io.File;

public abstract class AbstractExecutableCommand implements IExecutableCommand
{
	protected File executableFile;
	
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
	public File getExecutableFile()
	{
		return executableFile;
	}

	/* (non-Javadoc)
	 * @see descent.core.builder.IExecutableCommand#setExecutableName(java.lang.String)
	 */
	public void setExecutableFile(File executableFile)
	{
		this.executableFile = executableFile;
	}
}
