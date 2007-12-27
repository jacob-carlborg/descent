package descent.internal.compiler.lookup;

import descent.core.ICompilationUnit;
import descent.internal.compiler.env.IModuleFinder;
import descent.internal.compiler.env.INameEnvironment;
import descent.internal.compiler.parser.IModule;
import descent.internal.compiler.parser.SemanticContext;

/*
 * Finds modules the Descent way.
 */
public class DescentModuleFinder implements IModuleFinder {
	
	private final INameEnvironment environment;

	public DescentModuleFinder(INameEnvironment environment) {
		this.environment = environment;
	}

	public IModule findModule(char[][] compoundName, SemanticContext context) {
		ICompilationUnit unit = environment.findCompilationUnit(compoundName);
		if (unit != null){
			return new RModule(unit, context);
		} else{
			return null;
		}
	}

}
