package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;

public class ModuleInfoDeclaration extends VarDeclaration {
	
	public Module mod;
	
	public ModuleInfoDeclaration(Loc loc, Module mod, SemanticContext context) {
		super(loc, context.moduleinfo.type, mod.ident, null);
		this.mod = mod;
	}
	
	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		Assert.isTrue(false);
		return null;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		
	}

}
