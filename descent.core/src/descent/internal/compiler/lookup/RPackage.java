package descent.internal.compiler.lookup;

import descent.core.IJavaElement;
import descent.internal.compiler.parser.IPackage;

public class RPackage extends RScopeDsymbol implements IPackage {

	public RPackage(IJavaElement element) {
		super(element);
	}
	
	@Override
	public IPackage isPackage() {
		return this;
	}

}
