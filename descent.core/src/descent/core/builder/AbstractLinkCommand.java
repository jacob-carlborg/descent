package descent.core.builder;

public abstract class AbstractLinkCommand extends AbstractBuildCommand 
	implements ILinkCommand
{
	protected String outputFilename;

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
	 * @see descent.core.builder.ILinkCommand#getOutputFilename()
	 */
	public String getOutputFilename()
	{
		return outputFilename;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ILinkCommand#setOutputFilename(java.lang.String)
	 */
	public void setOutputFilename(String outputFilename)
	{
		this.outputFilename = outputFilename;
	}
}
