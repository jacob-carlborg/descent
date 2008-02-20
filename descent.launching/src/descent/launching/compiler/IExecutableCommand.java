package descent.launching.compiler;

import java.io.File;

/**
 * An object representing an executable command factory. Options are set by
 * using methods in this interface and subinterfaces, then {@link #getCommand()}
 * can be called to get a String representing the actual command to execute.
 * 
 * @author Robert Fraser
 */
public interface IExecutableCommand
{
	/**
	 * Gets a valid system command to be executed representing this object.
	 * Precondition: {@link #isValid()} returns true.
	 * 
	 * @return command represented by this object
	 */
	public String getCommand();
	
	/**
	 * Sets the defaults for all values in the command. Should be called in
	 * the constructor of all implementing classes. When called on an object
	 * of this interface that has been mdified, should return that object to
	 * the state just after construction. Implementors should call
	 * super.setDefaults() in all but the highest level of the hierarchy so
	 * each level of the hierarchy can reset its own deault values.
	 */
	public void setDefaults();
	
	/**
	 * Checks whether this command is ready to be run. Implementing classes
	 * should make their own checks and call super.isValid() to ensure the
	 * entire class hierarchy is valid.
	 * 
	 * @return true if and only if this is a valid command (ready to be run)
	 */
	public boolean isValid();
	
	// The path of the executable file to run
	public File getExecutableFile();
	public void setExecutableFile(File executableFile);
}
