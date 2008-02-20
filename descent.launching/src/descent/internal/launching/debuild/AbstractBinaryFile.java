package descent.internal.launching.debuild;

import java.io.File;

import org.eclipse.core.runtime.IPath;

import descent.core.IJavaProject;
import descent.core.JavaModelException;
import descent.internal.launching.LaunchingPlugin;

/**
 * Represents a binary file which may or may not already exist, and may or
 * may not be up to date. Information about what the binary file should be
 * (i.e. shoult it have ubnittests compiled in, should it be optimized, etc.)
 * should be filled in, in subclasses.  
 *
 * @author Robert Fraser
 */
public abstract class AbstractBinaryFile
{
	protected IJavaProject proj;
	
	/**
	 * Creates a new binary file associated with a project. The file will be
	 * located somewhere relative to the project's output directory as returned
	 * by {@link IJavaProject#getOutputLocation()}.
	 * 
	 * @param proj the project whose output directoy the file is located
	 *             relative to
	 */
	protected AbstractBinaryFile(IJavaProject proj)
	{
		this.proj = proj;
	}
	
	/**
	 * Checks whether the params specifying the file in the class represent
	 * a valid and unique binary file for the project.
	 * 
	 * @return true if and only if the binary file specified is valid
	 */
	public boolean isValid()
	{
		return null != proj && proj.exists();
	}
	
	/**
	 * Gets the handle to the file, which may or may not exist. File will be
	 * be an absolute path generally in the project's "bin" directory.
	 * 
	 * @return        the handle to the file
	 */
	public final File getFile()
	{
		try
		{
			IPath outputLocation = proj.getOutputLocation();
			StringBuffer path = new StringBuffer();
			path.append(outputLocation.makeAbsolute().toPortableString());
			path.append(IPath.SEPARATOR);
			path.append(getFilename());
			return new File(path.toString());
		}
		catch(JavaModelException e)
		{
			LaunchingPlugin.log(e);
			return null;
		}
	}
	
	/**
	 * Checks whether this file needs to be built. This will return true
	 * either if the file does not exist or if its last modification datetime
	 * is before the given datetime.
	 * 
	 * @param date the datetime of the earliest modification to be accepted,
	 *             in milliseconds
	 * @return     true if and only if the file exists and is up to date
	 */
	public final boolean shouldBuild(long date)
	{
		File file = getFile();
		
		if(!file.exists())
			return true;
		
		if(file.lastModified() < date)
			return true;
		
		return false;
	}
	
	/**
	 * Gets the name of the file without the path (but with the extension)
	 * 
	 * @return the name of the file.
	 */
	public abstract String getFilename();
}
