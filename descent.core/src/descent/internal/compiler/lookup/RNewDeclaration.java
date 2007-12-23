package descent.internal.compiler.lookup;

import descent.core.IMethod;
import descent.internal.compiler.parser.INewDeclaration;

public class RNewDeclaration extends RFuncDeclaration implements INewDeclaration {

	public RNewDeclaration(IMethod element) {
		super(element);
	}

	@Override
	public INewDeclaration isNewDeclaration() {
		return this;
	}
	
}
