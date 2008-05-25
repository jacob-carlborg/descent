package descent.building.compiler;

import java.io.File;

public abstract class AbstractLinkCommand extends AbstractBuildCommand 
	implements ILinkCommand
{
	protected File outputFilename;

	@Override
	public void setDefaults()
	{
		outputFilename = null;
		super.setDefaults();
	}
	
	@Override
	public boolean isValid()
	{
		if(outputFilename == null)
			return false;
		
		return super.isValid();
	}
	
	/* (non-Javadoc)
	 * @see descent.launching.compiler.ILinkCommand#getOutputFilename()
	 */
	public File getOutputFilename()
	{
		return outputFilename;
	}
	/* (non-Javadoc)
	 * @see descent.launching.compiler.ILinkCommand#setOutputFilename(java.lang.String)
	 */
	public void setOutputFilename(File outputFilename)
	{
		this.outputFilename = outputFilename;
	}
}
