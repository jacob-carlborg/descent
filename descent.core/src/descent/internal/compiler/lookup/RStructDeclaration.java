package descent.internal.compiler.lookup;

import descent.core.IType;
import descent.internal.compiler.parser.IStructDeclaration;

public class RStructDeclaration extends RAggregateDeclaration implements IStructDeclaration {

	public RStructDeclaration(IType element) {
		super(element);
	}
	
	@Override
	public IStructDeclaration isStructDeclaration() {
		return this;
	}

}
