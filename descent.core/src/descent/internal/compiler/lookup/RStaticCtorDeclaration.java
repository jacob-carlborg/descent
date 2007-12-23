package descent.internal.compiler.lookup;

import descent.core.IMethod;
import descent.internal.compiler.parser.IStaticCtorDeclaration;

public class RStaticCtorDeclaration extends RFuncDeclaration implements IStaticCtorDeclaration {

	public RStaticCtorDeclaration(IMethod element) {
		super(element);
	}
	
	@Override
	public IStaticCtorDeclaration isStaticCtorDeclaration() {
		return this;
	}

}
