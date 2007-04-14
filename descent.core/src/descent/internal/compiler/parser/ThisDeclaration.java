package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;

public class ThisDeclaration extends VarDeclaration {

	public ThisDeclaration(Loc loc, Type type) {
		super(loc, type, Id.This, null);
	}
	
	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		Assert.isTrue(false);
		return null;
	}

}
