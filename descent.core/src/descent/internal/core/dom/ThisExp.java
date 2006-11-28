package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IThisExpression;

public class ThisExp extends Expression implements IThisExpression {

	public ThisExp() {
		
	}
	
	public int getElementType() {
		return THIS_EXPRESSION;
	}
	
	@Override
	public String toString() {
		return "this";
	}
	
	@Override
	public void accept0(ElementVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
