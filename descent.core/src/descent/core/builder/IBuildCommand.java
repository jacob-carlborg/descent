package descent.core.builder;

import java.util.List;

/**
 * Represents a build command that takes a list of files (or even just one
 * file) and builds based on that.
 *
 * @author Robert Fraser
 */
public interface IBuildCommand extends IExecutableCommand
{
	public List<String> getFiles();
	public void addFile(String file);
}
