package descent.building.compiler;

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
	 * @see descent.launching.compiler.IExecutableCommand#getExecutableName()
	 */
	public File getExecutableFile()
	{
		return executableFile;
	}

	/* (non-Javadoc)
	 * @see descent.launching.compiler.IExecutableCommand#setExecutableName(java.lang.String)
	 */
	public void setExecutableFile(File executableFile)
	{
		this.executableFile = executableFile;
	}
}
