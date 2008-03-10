package descent.internal.launching.debuild;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

/**
 * Groups multiple object files together to be executed as a single compilation
 * command (note that they're not actually object files, instead source files
 * :-)).
 * 
 * @author Robert Fraser
 */
public class GroupedCompile
{   
	private final List<ObjectFile> objectFiles = new ArrayList<ObjectFile>();
	
	/**
	 * Adds the object file to this command's list of object files if and only
	 * if the object file hasn't already been added.
	 * 
	 * @param obj the object file to add
	 */
	public void addObjectFile(ObjectFile obj)
	{
        if(DebuildBuilder.DEBUG)
            Assert.isTrue(obj.shouldBuild());
        
		if(!objectFiles.contains(obj))
			objectFiles.add(obj);
	}
	
	/**
	 * Gets the list of object files added so far.
	 */
	public List<ObjectFile> getObjectFiles()
	{
		return objectFiles;
	}
}
