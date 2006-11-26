package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IDollarExpression;

public class DollarExp extends Expression implements IDollarExpression {

	public DollarExp(Loc loc) {
		// TODO Auto-generated constructor stub
	}
	
	public int getElementType() {
		return DOLAR_EXPRESSION;
	}
	
	@Override
	void accept0(ElementVisitor visitor) {
		// TODO Auto-generated method stub
		
	}

}
