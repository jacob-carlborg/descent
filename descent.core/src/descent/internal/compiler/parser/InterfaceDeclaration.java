package descent.internal.compiler.parser;

import java.util.List;

public class InterfaceDeclaration extends AggregateDeclaration {

	public List<BaseClass> baseClasses;

	public InterfaceDeclaration(IdentifierExp id, List<BaseClass> baseClasses) {
		super(id);
		this.baseClasses = baseClasses;
	}
	
	@Override
	public InterfaceDeclaration isInterfaceDeclaration() {
		return this;
	}
	
	@Override
	public int kind() {
		return INTERFACE_DECLARATION;
	}

}
