package descent.building.compiler;

/**
 * Gets information about a compiler (or a compiler suite/tool chain) used in
 * building. For example, gets info about how the commands are constructed
 * for that particular compiler, how to parse the output, etc. Generally,
 * implementations of this will be a singleton, since only one compiler of a
 * given type exists.
 * 
 * Clients may discovr the compiler interface for a given compiler by calling
 * {@link IVMInstall#getCompilerInterface()}
 * 
 * @author Robert Fraser
 */
public interface ICompilerInterface
{
	/**
	 * Creates a new compile command of the correct type for this compiler.
	 */
	public ICompileCommand createCompileCommand();
	
	/**
	 * Creates a new link command of the correct type for this compiler.
	 */
	public ILinkCommand createLinkCommand();
	
	/**
	 * Creates a new response interpreter of the correct type for
	 * this compiler.
	 */
	public IResponseInterpreter createCompileResponseInterpreter();
	
	/**
	 * Creates a new response interpreter of the correct type for this
	 * linker.
	 */
	public IResponseInterpreter createLinkResponseInterpreter();
	
	/**
	 * Gets the compiler options which should be presented in the UI.
	 * 
	 * @return the ordered list of compiler options to be presented in the UI.
	 */
	// TODO comment about what sorts of options should be included etc
	public CompilerOption[] getOptions();
}
