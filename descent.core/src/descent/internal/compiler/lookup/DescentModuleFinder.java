package descent.internal.compiler.lookup;

import descent.core.ICompilationUnit;
import descent.internal.compiler.env.IModuleFinder;
import descent.internal.compiler.env.INameEnvironment;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.core.CompilerConfiguration;

public class DescentModuleFinder implements IModuleFinder {

	private final INameEnvironment environment;
	private final ModuleBuilder builder;

	public DescentModuleFinder(INameEnvironment environment, CompilerConfiguration config) {
		this.environment = environment;
		this.builder = new ModuleBuilder(config);
	}

	public Module findModule(char[][] compoundName, SemanticContext context) {
		ICompilationUnit unit = environment.findCompilationUnit(compoundName);
		if (unit != null){
			return builder.build(unit);
		}
		return null;
	}
	
}
