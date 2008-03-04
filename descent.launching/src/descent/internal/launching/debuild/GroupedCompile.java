package descent.internal.launching.debuild;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import descent.launching.compiler.ICompileCommand;

/**
 * Groups multiple object files together in a single compile command that can be executed.
 * 
 * @author Robert Fraser
 */
public class GroupedCompile
{
	private final List<ObjectFile> objectFiles = new ArrayList<ObjectFile>();
	private final CompileOptions opts;
	
	public GroupedCompile(CompileOptions opts)
	{
		this.opts = opts;
	}
	
	/**
	 * Adds the object file to this command's list of object files if and only
	 * if the object file hasn't already been added. Also sets the object's
	 * compile options to those of this compile group.
	 * 
	 * @param obj the object file to add
	 */
	public void addObjectFile(ObjectFile obj)
	{
		if(!objectFiles.contains(obj))
		{
			obj.setOptions(opts);
			objectFiles.add(obj);
		}
	}
	
	/**
	 * Gets the list of object files added so far.
	 */
	public List<ObjectFile> getObjectFiles()
	{
		return objectFiles;
	}
}
