package descent.internal.compiler.lookup;

import descent.core.IMethod;
import descent.internal.compiler.parser.IDeleteDeclaration;
import descent.internal.compiler.parser.SemanticContext;

public class RDeleteDeclaration extends RFuncDeclaration implements IDeleteDeclaration {

	public RDeleteDeclaration(IMethod element, SemanticContext context) {
		super(element, context);
	}
	
	@Override
	public String kind() {
		return "deallocator";
	}

}
