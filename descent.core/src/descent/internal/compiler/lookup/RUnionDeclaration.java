package descent.internal.compiler.lookup;

import descent.core.IType;
import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.IUnionDeclaration;
import descent.internal.compiler.parser.SemanticContext;

public class RUnionDeclaration extends RStructDeclaration implements IUnionDeclaration {

	public RUnionDeclaration(IType element, SemanticContext context) {
		super(element, context);
	}
	
	@Override
	public IUnionDeclaration isUnionDeclaration() {
		return this;
	}
	
	public char getSignaturePrefix() {
		return ISignatureConstants.UNION;
	}
	
	@Override
	public String kind() {
		return "union";
	}

}
