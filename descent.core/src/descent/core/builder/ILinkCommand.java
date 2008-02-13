package descent.core.builder;

import java.io.File;

/**
 * Represents a D linker command
 * 
 * @author Robert Fraser
 */
public interface ILinkCommand extends IBuildCommand
{
	// The name of the resulting output file
	public File getOutputFilename();
	public void setOutputFilename(File outputFilename);
	
	// TODO more interesting stuff
}
