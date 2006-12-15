package descent.internal.core.dom;

import descent.core.dom.ISuperExpression;
import descent.core.domX.ASTVisitor;

public class SuperExp extends Expression implements ISuperExpression {

	public SuperExp() {
		
	}
	
	public int getElementType() {
		return ElementTypes.SUPER_EXPRESSION;
	}
	
	@Override
	public String toString() {
		return "super";
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
