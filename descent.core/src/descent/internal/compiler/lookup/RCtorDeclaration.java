package descent.internal.compiler.lookup;

import descent.core.IMethod;
import descent.internal.compiler.parser.ICtorDeclaration;

public class RCtorDeclaration extends RFuncDeclaration implements ICtorDeclaration {

	public RCtorDeclaration(IMethod element) {
		super(element);
	}
	
	@Override
	public ICtorDeclaration isCtorDeclaration() {
		return this;
	}

}
