package descent.internal.compiler.lookup;

import descent.core.IJavaElement;
import descent.internal.compiler.parser.IImport;
import descent.internal.compiler.parser.IModule;
import descent.internal.compiler.parser.IPackage;
import descent.internal.compiler.parser.SemanticContext;

public class RImport extends RDsymbol implements IImport {

	public RImport(IJavaElement element, SemanticContext context) {
		super(element, context);
	}

	public IModule mod() {
		// TODO Auto-generated method stub
		return null;
	}

	public IPackage pkg() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IImport isImport() {
		return this;
	}

}
