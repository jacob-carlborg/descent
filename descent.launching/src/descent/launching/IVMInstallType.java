package descent.launching;

import java.io.File;

import org.eclipse.core.runtime.IStatus;

/**
 * Represents a particular type of compiler for which there may be
 * any number of compiler installations. An example of a compiler type
 * is DMD which might have instances corresponding 
 * to different installed versions such as DMD 1.0 and
 * DMD 2.0.
 * <p>
 * This interface is intended to be implemented by clients that contribute
 * to the <code>"descent.core.compilerType"</code> extension point.
 * </p>
 * 
 * @see	IVMInstall
 */
public interface IVMInstallType {
	
	/**
	 * Creates a new instance of this compiler Install type.
	 * The newly created ICompilerInstall is managed by this ICompilerInstallType.
	 * 
	 * @param	id	An id String that must be unique within this ICompilerInstallType.
	 * 
	 * @return the newly created compiler instance
	 * 
	 * @throws	IllegalArgumentException	If the id exists already.
	 */
	IVMInstall createVMInstall(String id);
	/**
	 * Finds the compiler with the given id.
	 * 
	 * @param id the compiler id
	 * @return a compiler instance, or <code>null</code> if not found
	 */
	IVMInstall findVMInstall(String id);
	/**
	 * Finds the compiler with the given name.
	 * 
	 * @param name the compiler name
	 * @return a compiler instance, or <code>null</code> if not found
	 * @since 2.0
	 */
	IVMInstall findVMInstallByName(String name);	
	
	/**
	 * Remove the compiler associated with the given id from the set of compilers managed by
	 * this compiler type. Has no effect if a compiler with the given id is not currently managed
	 * by this type.
	 * A compiler install that is disposed may not be used anymore.
	 * 
	 * @param id the id of the compiler to be disposed.
	 */
	void disposeVMInstall(String id);
	/**
	 * Returns all compiler instances managed by this compiler type.
	 * 
	 * @return the list of compiler instances managed by this compiler type
	 */
	IVMInstall[] getVMInstalls();
	/**
	 * Returns the display name of this compiler type.
	 * 
	 * @return the name of this ICompilerInstallType
	 */ 
	String getName();
	
	/**
	 * Returns the globally unique id of this VM type.
	 * Clients are responsible for providing a unique id.
	 * 
	 * @return the id of this ICompilerInstallType
	 */ 
	String getId();
	/**
	 * Validates the given location of a compiler installation.
	 * <p>
	 * For example, an implementation might check whether the compiler executable 
	 * is present.
	 * </p>
	 * 
	 * @param installLocation the root directory of a potential installation for
	 *   this type of compiler
	 * @return a status object describing whether the install location is valid
	 */
	IStatus validateInstallLocation(File installLocation);
		
	/**
	 * Returns a collection of <code>LibraryLocation</code>s that represent the
	 * default system libraries of this compiler install type, if a compiler was installed
	 * at the given <code>installLocation</code>.
	 * The returned <code>LibraryLocation</code>s may not exist if the
	 * <code>installLocation</code> is not a valid install location.
	 * 
	 * @param installLocation home location
	 * @see LibraryLocation
	 * @see IVMInstallType#validateInstallLocation(File)
	 * 
	 * @return default library locations based on the given <code>installLocation</code>.
	 * @since 2.0
	 */
	LibraryLocation[] getDefaultLibraryLocations(File installLocation);	

}
