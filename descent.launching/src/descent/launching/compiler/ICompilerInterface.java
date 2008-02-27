package descent.launching.compiler;

import descent.launching.IVMInstall;

/**
 * Gets information about a compiler (or a compiler suite/tool chain) used in
 * building. For example, gets info about how the commands are constructed
 * for that particular compiler, how to parse the output, etc. Generally,
 * implementations of this will be a singleton, since only one compiler of a
 * given type exists.
 * 
 * Clients may discovr the compiler interface for a given compilr by calling
 * {@link IVMInstall#getCompilerInterface()}
 * 
 * @author Robert Fraser
 */
public interface ICompilerInterface
{	
	/**
	 * Creates an interpreter that will interpret the command-line results
	 * of calling Rebuild.
	 */
	public IResponseInterpreter createRebuildResponseInterpreter();
}
