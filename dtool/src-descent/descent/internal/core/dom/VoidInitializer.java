package descent.internal.core.dom;

import descent.core.dom.IVoidInitializer;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;

public class VoidInitializer extends Initializer implements IVoidInitializer {

	public VoidInitializer() {
		
	}
	
	public int getElementType() {
		return ElementTypes.VOID_INITIALIZER;
	}
	
	public void accept0(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
