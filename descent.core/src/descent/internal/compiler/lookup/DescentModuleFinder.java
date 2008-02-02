package descent.internal.compiler.lookup;

import descent.core.ICompilationUnit;
import descent.core.compiler.CharOperation;
import descent.internal.codeassist.ISearchRequestor;
import descent.internal.compiler.env.AccessRestriction;
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
	private final HashtableOfCharArrayAndObject moduleCache = new HashtableOfCharArrayAndObject();
	private final HashtableOfCharArrayAndObject hintsCache = new HashtableOfCharArrayAndObject();

	public DescentModuleFinder(INameEnvironment environment) {
		this.environment = environment;
	}
	
	public boolean isLoaded(char[][] compoundName) {
		char[] name = CharOperation.concatWith(compoundName, '.');
		return moduleCache.get(name) != null;
	}

	public IModule findModule(char[][] compoundName, SemanticContext context) {
		char[] name = CharOperation.concatWith(compoundName, '.');
		IModule mod = (IModule) moduleCache.get(name);
		if (mod == null) {
			ICompilationUnit unit = environment.findCompilationUnit(compoundName);
			if (unit != null){
				mod = new RModule(unit, context);
				moduleCache.put(name, mod);
			} else{
				mod = null;
			}
		}
		return mod;
	}
	
	public HashtableOfCharArrayAndObject getHints(char[] ident) {
		HashtableOfCharArrayAndObject hints = (HashtableOfCharArrayAndObject) hintsCache.get(ident);
		if (hints == null) {
//			long time = System.currentTimeMillis();
			
			final HashtableOfCharArrayAndObject hintsFinal = new HashtableOfCharArrayAndObject();
			
			environment.findDeclarations(ident, new ISearchRequestor() {
				public void acceptField(char[] packageName, char[] name, char[] typeName, char[][] enclosingTypeNames, long modifiers, AccessRestriction accessRestriction) {
					hintsFinal.put(packageName, this);
				}
				public void acceptMethod(char[] packageName, char[] name, char[][] enclosingTypeNames, char[] signature, long modifiers, AccessRestriction accessRestriction) {
					hintsFinal.put(packageName, this);
				}
				public void acceptType(char[] packageName, char[] typeName, char[][] enclosingTypeNames, long modifiers, AccessRestriction accessRestriction) {
					hintsFinal.put(packageName, this);
				}
				public void acceptCompilationUnit(char[] fullyQualifiedName) {
				}
				public void acceptPackage(char[] packageName) {
					
				}
			});
			
			hints = hintsFinal;
			
//			time = System.currentTimeMillis() - time;
//			System.out.println("Hints time for " + new String(ident) + ": " + time);
//			System.out.println("Results:");
//			for(char[] hint : hints.keys()) {
//				if (hint != null) {
//					System.out.println(" - " + new String(hint));
//				}
//			}
			
			hintsCache.put(ident, hints);
		}
		
		return hints;
	}

}
