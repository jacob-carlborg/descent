package descent.core.builder;

/**
 * Gets information about a compiler (or a compiler suite/tool chain) used in
 * building. For example, gets info about how the commands are constructed
 * for that particular compiler, how to parse the output, etc. Generally,
 * implementations of this will be a singleton, since only one compiler of a
 * given type exists.
 * 
 * TODO make this an extension point once its relationship to descent.launching
 * has been established.
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
	 * Creates a new compiler response interpreter of the correct type for
	 * this compiler.
	 */
	public ICompileResponseInterpreter createCompileResponseInterpreter();
	
	/**
	 * Creates a new linker response interpreter of the correct type for this
	 * compiler.
	 */
	public ILinkResponseInterpreter createLinkResponseInterpreter();
}
