package descent.internal.compiler.lookup;

import descent.core.IType;
import descent.internal.compiler.parser.IUnionDeclaration;

public class RUnionDeclaration extends RStructDeclaration implements IUnionDeclaration {

	public RUnionDeclaration(IType element) {
		super(element);
	}
	
	@Override
	public IUnionDeclaration isUnionDeclaration() {
		return this;
	}

}
