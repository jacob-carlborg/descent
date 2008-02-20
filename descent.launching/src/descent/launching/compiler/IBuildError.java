package descent.launching.compiler;

/**
 * Represents an error encountered while building a project. 
 *
 * @author Robert Fraser
 */
public interface IBuildError
{
	/**
	 * Gets the error message. Should never be null.
	 * 
	 * PERHAPS i18n?
	 */
	public abstract String getMessage();
	
	/**
	 * Gets the file the error occured in or null if the file is not availible.
	 * This should be a valid path either relative to the compiler working
	 * directory or absolute, but it does not necescarily need to refer to an
	 * Eclipse-managed resource (i.e. it could refer to a standard library
	 * location, etc.), or even an existing file, so clients should validate
	 * this.
	 */
	public abstract String getFile();
	
	/**
	 * Gets the line or -1 if the line is not availible. Will only be valid
	 * if {@link #getFile} is valid, since this refers to a line in that file,
	 * but may be invalid even if {@link #getFile} is valid (if line info
	 * couldn't be found for an error).
	 */
	public abstract int getLine();

}