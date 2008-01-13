package descent.internal.compiler.lookup;

import descent.core.ICompilationUnit;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.env.IModuleFinder;
import descent.internal.compiler.env.INameEnvironment;
import descent.internal.compiler.parser.HashtableOfCharArrayAndObject;
import descent.internal.compiler.parser.IModule;
import descent.internal.compiler.parser.SemanticContext;

/*
 * Finds modules the Descent way.
 */
public class DescentModuleFinder implements IModuleFinder {
	
	private final INameEnvironment environment;
	
	/*
	 * Cache results for speedup, and also for not loading multiple
	 * times the same module.
	 */
	private final HashtableOfCharArrayAndObject cache = new HashtableOfCharArrayAndObject();

	public DescentModuleFinder(INameEnvironment environment) {
		this.environment = environment;
	}
	
	public boolean isLoaded(char[][] compoundName) {
		char[] name = CharOperation.concatWith(compoundName, '.');
		return cache.get(name) == null;
	}

	public IModule findModule(char[][] compoundName, SemanticContext context) {
		char[] name = CharOperation.concatWith(compoundName, '.');
		IModule mod = (IModule) cache.get(name);
		if (mod == null) {
			ICompilationUnit unit = environment.findCompilationUnit(compoundName);
			if (unit != null){
				mod = new RModule(unit, context);
				cache.put(name, mod);
			} else{
				mod = null;
			}
		}
		return mod;
	}

}
