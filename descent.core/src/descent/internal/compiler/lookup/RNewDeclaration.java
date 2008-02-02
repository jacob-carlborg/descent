package descent.internal.compiler.lookup;

import descent.core.IMethod;
import descent.internal.compiler.parser.INewDeclaration;
import descent.internal.compiler.parser.SemanticContext;

public class RNewDeclaration extends RFuncDeclaration implements INewDeclaration {

	public RNewDeclaration(IMethod element, SemanticContext context) {
		super(element, context);
	}

	@Override
	public INewDeclaration isNewDeclaration() {
		return this;
	}
	
	@Override
	public String kind() {
		return "allocator";
	}
	
}
