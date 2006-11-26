package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IVoidInitializer;

public class VoidInitializer extends Initializer implements IVoidInitializer {

	public VoidInitializer(Loc loc) {
		// TODO Auto-generated constructor stub
	}
	
	public int getElementType() {
		return VOID_INITIALIZER;
	}
	
	public void accept0(ElementVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
