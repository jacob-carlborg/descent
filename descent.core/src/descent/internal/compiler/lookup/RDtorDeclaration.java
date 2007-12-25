package descent.internal.compiler.lookup;

import descent.core.IMethod;
import descent.internal.compiler.parser.IDtorDeclaration;
import descent.internal.compiler.parser.SemanticContext;

public class RDtorDeclaration extends RFuncDeclaration implements IDtorDeclaration {

	public RDtorDeclaration(IMethod element, SemanticContext context) {
		super(element, context);
	}

}
