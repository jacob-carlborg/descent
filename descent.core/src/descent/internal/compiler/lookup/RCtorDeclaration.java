package descent.internal.compiler.lookup;

import descent.core.IMethod;
import descent.internal.compiler.parser.ICtorDeclaration;
import descent.internal.compiler.parser.SemanticContext;

public class RCtorDeclaration extends RFuncDeclaration implements ICtorDeclaration {

	public RCtorDeclaration(IMethod element, SemanticContext context) {
		super(element, context);
	}
	
	@Override
	public ICtorDeclaration isCtorDeclaration() {
		return this;
	}
	
	@Override
	public String toChars(SemanticContext context) {
		return "this";
	}
	
	@Override
	public String kind() {
		return "constructor";
	}

}
