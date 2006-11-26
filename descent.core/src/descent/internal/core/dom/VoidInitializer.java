package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;

public class VoidInitializer extends Initializer {

	public VoidInitializer(Loc loc) {
		// TODO Auto-generated constructor stub
	}
	
	public int getElementType() {
		return VOID_INITIALIZER;
	}
	
	public void accept(IDElementVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
