package descent.internal.compiler.lookup;

import descent.core.IType;
import descent.internal.compiler.parser.IInterfaceDeclaration;
import descent.internal.compiler.parser.SemanticContext;

public class RInterfaceDeclaration extends RClassDeclaration implements IInterfaceDeclaration {

	public RInterfaceDeclaration(IType element, SemanticContext context) {
		super(element, context);
	}
	
	@Override
	public IInterfaceDeclaration isInterfaceDeclaration() {
		return this;
	}

}
