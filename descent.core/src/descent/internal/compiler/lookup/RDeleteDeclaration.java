package descent.internal.compiler.lookup;

import descent.core.IMethod;
import descent.internal.compiler.parser.IDeleteDeclaration;

public class RDeleteDeclaration extends RFuncDeclaration implements IDeleteDeclaration {

	public RDeleteDeclaration(IMethod element) {
		super(element);
	}

}
