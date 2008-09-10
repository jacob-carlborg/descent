package descent.building.compiler;

import descent.building.compiler.ui.CompilerOption;

/**
 * Gets information about a compiler (or a compiler suite/tool chain) used in
 * building. For example, gets info about how the commands are constructed
 * for that particular compiler, how to parse the output, etc. Generally,
 * implementations of this will be a singleton, since only one compiler of a
 * given type exists, and only a single instance of this will be constructed by
 * the building plugin.
 * 
 * To get the shared instances of compiler interfaces, use the
 * {@link descent.building.CompilerInterfaceRegistry} class.
 * 
 * DOCS moar
 * 
 * <b>This class should be implemented by clients providing a compiler interface.</b>
 * <b>This class does not need to be thread-safe.</b>
 * 
 * @author Robert Fraser
 */
public interface ICompilerInterface
{   
	/**
	 * Gets the compiler options which should be presented in the UI.
	 * 
	 * @return the ordered list of compiler options to be presented in the UI.
	 */
	// DOCS comment about what sorts of options should be included etc
	public CompilerOption[] getOptions();
	
	/**
	 * Gets a new instance of a compile manager that should cooperate with the
	 * given build manager.
	 * 
	 * @param buildManager the build manager the compile manager should work
	 *                     with
	 * @return             an instance of a compiler manager to be used for a
	 *                     build
	 * @see                ICompileManager
	 */
	public ICompileManager getCompileManager(IBuildManager buildManager);
}
