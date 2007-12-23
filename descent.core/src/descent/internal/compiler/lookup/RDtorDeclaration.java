package descent.internal.compiler.lookup;

import descent.core.IMethod;
import descent.internal.compiler.parser.IDtorDeclaration;

public class RDtorDeclaration extends RFuncDeclaration implements IDtorDeclaration {

	public RDtorDeclaration(IMethod element) {
		super(element);
	}

}
