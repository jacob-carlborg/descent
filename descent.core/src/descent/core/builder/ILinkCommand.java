package descent.core.builder;

/**
 * Represents a D linker command
 * 
 * @author Robert Fraser
 */
public interface ILinkCommand extends IBuildCommand
{
	// The name of the resulting output file
	public String getOutputFilename();
	public void setOutputFilename(String outputFilename);
	
	// TODO more interesting stuff
}
