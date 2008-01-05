package descent.internal.compiler.lookup;

import descent.core.IMethod;
import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.IStaticCtorDeclaration;
import descent.internal.compiler.parser.SemanticContext;

public class RStaticCtorDeclaration extends RFuncDeclaration implements IStaticCtorDeclaration {

	public RStaticCtorDeclaration(IMethod element, SemanticContext context) {
		super(element, context);
	}
	
	@Override
	public IStaticCtorDeclaration isStaticCtorDeclaration() {
		return this;
	}
	
	@Override
	public char getSignaturePrefix() {
		return ISignatureConstants.OTHER;
	}

}
