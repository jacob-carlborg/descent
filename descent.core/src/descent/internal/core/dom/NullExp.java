package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.INullExpression;

public class NullExp extends Expression implements INullExpression {

	public NullExp() {
		// TODO Auto-generated constructor stub
	}
	
	public int getElementType() {
		return NULL_EXPRESSION;
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public String toString() {
		return "null";
	}

}
