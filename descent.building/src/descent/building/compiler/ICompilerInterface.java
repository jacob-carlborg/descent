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
 * TODO document how it is used
 * 
 * <b>This class should be implemented by clients providing a compiler interface.</b>
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
	// TODO comment about what sorts of options should be included etc
	public CompilerOption[] getOptions();
	
	/**
	 * Checks whether this type of compiler supports internal compiler analysis.
	 * Internal dependency analysis means that during the compile step it is
	 * possible to retrieve a list of dependencies after building an object file.
	 * For example, DMD output can be parsed for imports. If this returns false,
	 * the descent builder will use internal dependency analysis. This method can
	 * be slower and could potentially have issues if the semantics are slightly
	 * different than the compiler, so if it is possible to support internal
	 * dependency analysis, it should be done.
	 * 
	 * @return true if and only if this compiler supports internal dependency
	 *         analysis
	 */
	public boolean supportsInternalDependancyAnalysis();
	
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
