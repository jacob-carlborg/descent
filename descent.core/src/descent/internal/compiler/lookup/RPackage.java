package descent.internal.compiler.lookup;

import descent.core.IJavaElement;
import descent.internal.compiler.parser.IPackage;
import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.SemanticContext;

public class RPackage extends RScopeDsymbol implements IPackage {

	public RPackage(IJavaElement element, SemanticContext context) {
		super(element, context);
	}
	
	@Override
	public IPackage isPackage() {
		return this;
	}
	
	public char getSignaturePrefix() {
		return 0;
	}

}
