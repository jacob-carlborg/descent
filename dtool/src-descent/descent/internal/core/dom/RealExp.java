package descent.internal.core.dom;

import java.math.BigInteger;

import descent.core.dom.IRealExpression;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;

public class RealExp extends Expression implements IRealExpression {

	public RealExp(BigInteger numberValue, Type tfloat32) {
		// TODO Auto-generated constructor stub
	}
	
	public int getElementType() {
		return ElementTypes.REAL_EXPRESSION;
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
