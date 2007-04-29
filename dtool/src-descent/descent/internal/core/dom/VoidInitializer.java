package descent.internal.core.dom;

import descent.core.dom.IVoidInitializer;
import descent.core.domX.IASTVisitor;

public class VoidInitializer extends Initializer implements IVoidInitializer {

	public VoidInitializer() {
		
	}
	
	public int getElementType() {
		return ElementTypes.VOID_INITIALIZER;
	}
	
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
