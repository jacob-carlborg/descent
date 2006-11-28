package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IDollarExpression;

public class DollarExp extends Expression implements IDollarExpression {

	public DollarExp() {
	}
	
	public int getElementType() {
		return DOLAR_EXPRESSION;
	}
	
	@Override
	void accept0(ElementVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
