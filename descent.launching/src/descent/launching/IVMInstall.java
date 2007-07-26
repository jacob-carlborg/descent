package descent.launching;

import java.io.File;
import java.net.URL;

/**
 * Represents a particular instalation of a D compiler.
 */
public interface IVMInstall {
	
	/**
	 * Returns the id for this compiler. Compiler IDs are unique within the compilers 
	 * of a given compiler type. The compiler id is not intended to be presented to users.
	 * 
	 * @return the compiler identifier. Must not return <code>null</code>.
	 */
	String getId();
	
	/**
	 * Returns the display name of this compiler.
	 * The compiler name is intended to be presented to users.
	 * 
	 * @return the display name of this compiler. May return <code>null</code>.
	 */
	String getName();
	
	/**
	 * Sets the display name of this compiler.
	 * The compiler name is intended to be presented to users.
	 * 
	 * @param name the display name of this compiler
	 */
	void setName(String name);
	
	/**
	 * Returns the root directory of the install location of this compiler.
	 * 
	 * @return the root directory of this compiler installation. May
	 * 			return <code>null</code>.
	 */
	File getInstallLocation();
	
	/**
	 * Sets the root directory of the install location of this compiler.
	 * 
	 * @param installLocation the root directory of this compiler installation
	 */
	void setInstallLocation(File installLocation);
	
	/**
	 * Returns the library locations of this IVMInstall. Generally,
	 * clients should use <code>JavaRuntime.getLibraryLocations(IVMInstall)</code>
	 * to determine the libraries associated with this VM install.
	 * 
	 * @see IVMInstall#setLibraryLocations(LibraryLocation[])
	 * @return 	The library locations of this IVMInstall.
	 * 			Returns <code>null</code> to indicate that this VM install uses
	 * 			the default library locations associated with this VM's install type.
	 * @since 2.0
	 */
	LibraryLocation[] getLibraryLocations();	
	
	/**
	 * Sets the library locations of this IVMInstall.
	 * @param	locations The <code>LibraryLocation</code>s to associate
	 * 			with this IVMInstall.
	 * 			May be <code>null</code> to indicate that this VM install uses
	 * 			the default library locations associated with this VM's install type.
	 * @since 2.0
	 */
	void setLibraryLocations(LibraryLocation[] locations);	
	
	/**
	 * Sets the Javadoc location associated with this VM install.
	 * 
	 * @param url a url pointing to the Javadoc location associated with
	 * 	this VM install
	 * @since 2.0
	 */
	public void setJavadocLocation(URL url);
	
	/**
	 * Returns the Javadoc location associated with this VM install.
	 * 
	 * @return a url pointing to the Javadoc location associated with
	 * 	this VM install, or <code>null</code> if none
	 * @since 2.0
	 */
	public URL getJavadocLocation();
	
	/**
	 * Returns the compiler type of this compiler.
	 * 
	 * @return the compiler type that created this ICompilerInstall instance
	 */
	IVMInstallType getVMInstallType();
	
	/**
	 * Returns the d version of this compiler.
	 * @return the d version of this compiler
	 */
	String getJavaVersion();

}
