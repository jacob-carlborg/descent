package descent.internal.core.ctfe;

import descent.internal.compiler.lookup.ModuleBuilder;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Type;
import descent.internal.core.CompilerConfiguration;
import descent.internal.core.ctfe.dom.CompileTimeFuncDeclaration;

public class CompileTimeModuleBuilder extends ModuleBuilder {

	public CompileTimeModuleBuilder(CompilerConfiguration config, ASTNodeEncoder encoder) {
		super(config, encoder);
	}
	
	@Override
	protected FuncDeclaration newFuncDeclaration(Loc loc, IdentifierExp ident, int storageClass, Type type) {
		return new CompileTimeFuncDeclaration(loc, ident, storageClass, type);
	}

}
