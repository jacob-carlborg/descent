package descent.internal.compiler.parser;

import java.util.List;

public class InterfaceDeclaration extends ClassDeclaration {

	public InterfaceDeclaration(IdentifierExp id, List<BaseClass> baseclasses) {
		super(id, baseclasses);
		com = false;
	    if (id.ident == Id.IUnknown) {		// IUnknown is the root of all COM objects
	    	com = true;
	    }
	}
	
	@Override
	public InterfaceDeclaration isInterfaceDeclaration() {
		return this;
	}
	
	@Override
	public int kind() {
		return INTERFACE_DECLARATION;
	}

}
