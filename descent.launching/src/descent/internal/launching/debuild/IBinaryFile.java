package descent.internal.launching.debuild;

import java.io.File;

/**
 * Represents a binary file which may or may not already exist, and may or
 * may not be up to date. Information about what the binary file should be
 * (i.e. shoult it have ubnittests compiled in, should it be optimized, etc.)
 * should be filled in, in subclasses.  
 *
 * @author Robert Fraser
 */
public interface IBinaryFile
{
	/**
	 * Checks whether the params specifying the file in the class represent
	 * a valid and unique binary file for the project.
	 * 
	 * @return true if and only if the binary file specified is valid
	 */
	public boolean isValid();
	
	/**
	 * Gets the handle to the file, which may or may not exist. File will be
	 * be an absolute path generally in the project's "bin" directory.
	 * 
	 * @return        the handle to the file
	 */
	public File getFile();
	
	/**
	 * Gets the name of the file without the path.
	 * 
	 * @return the name of the file.
	 */
	public String getFilename();
	
	/**
	 * Checks whether this file needs to be built. This will return true
	 * either if the file does not exist or if its last modification datetime
	 * is before the given datetime.
	 * 
	 * @param date the datetime of the earliest modification to be accepted,
	 *             in milliseconds
	 * @return     true if and only if the file exists and is up to date
	 */
	public boolean shouldBuild(long date);
}
