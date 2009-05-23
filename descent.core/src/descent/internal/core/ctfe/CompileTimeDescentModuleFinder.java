package descent.internal.core.ctfe;

import descent.internal.compiler.env.INameEnvironment;
import descent.internal.compiler.lookup.DescentModuleFinder;
import descent.internal.compiler.lookup.ModuleBuilder;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.core.CompilerConfiguration;

public class CompileTimeDescentModuleFinder extends DescentModuleFinder {

	public CompileTimeDescentModuleFinder(INameEnvironment environment, CompilerConfiguration config, ASTNodeEncoder encoder) {
		super(environment, config, encoder);
	}
	
	@Override
	protected ModuleBuilder newModuleBuilder(CompilerConfiguration config, ASTNodeEncoder encoder) {
		return new CompileTimeModuleBuilder(config, encoder);
	}

}
