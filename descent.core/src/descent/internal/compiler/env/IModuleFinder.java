package descent.internal.compiler.env;

import descent.internal.compiler.parser.IModule;
import descent.internal.compiler.parser.SemanticContext;

/**
 * A module finder finds and loads modules.
 */
public interface IModuleFinder {
	
	/**
	 * Finds and loads the module denoted by the given compound name.
	 */
	IModule findModule(char[][] compoundName, SemanticContext context);

}
