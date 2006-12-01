package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.ISuperExpression;

public class SuperExp extends Expression implements ISuperExpression {

	public SuperExp() {
		
	}
	
	public int getElementType() {
		return SUPER_EXPRESSION;
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
