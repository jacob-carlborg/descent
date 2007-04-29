package descent.internal.core.dom;

import descent.core.dom.IDollarExpression;
import descent.core.domX.IASTVisitor;

public class DollarExp extends Expression implements IDollarExpression {

	public DollarExp() {
	}
	
	public int getElementType() {
		return ElementTypes.DOLAR_EXPRESSION;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
