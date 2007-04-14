package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;

public class ClassInfoDeclaration extends VarDeclaration {
	
	public ClassDeclaration cd;

	public ClassInfoDeclaration(Loc loc, ClassDeclaration cd, SemanticContext context) {
		super(loc, context.classinfo.type, cd.ident, null);
		this.cd = cd;
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
