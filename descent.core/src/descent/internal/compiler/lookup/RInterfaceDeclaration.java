package descent.internal.compiler.lookup;

import descent.core.IType;
import descent.internal.compiler.parser.IInterfaceDeclaration;

public class RInterfaceDeclaration extends RClassDeclaration implements IInterfaceDeclaration {

	public RInterfaceDeclaration(IType element) {
		super(element);
	}
	
	@Override
	public IInterfaceDeclaration isInterfaceDeclaration() {
		return this;
	}

}
