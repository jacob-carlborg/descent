package descent.internal.core.dom;

import descent.core.dom.IThisExpression;
import descent.core.domX.IASTVisitor;

public class ThisExp extends Expression implements IThisExpression {

	public ThisExp() {
		
	}
	
	public int getElementType() {
		return ElementTypes.THIS_EXPRESSION;
	}
	
	@Override
	public String toString() {
		return "this";
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
