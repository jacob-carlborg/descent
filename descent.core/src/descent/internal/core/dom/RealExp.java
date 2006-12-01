package descent.internal.core.dom;

import java.math.BigInteger;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IRealExpression;

public class RealExp extends Expression implements IRealExpression {

	public RealExp(BigInteger numberValue, Type tfloat32) {
		// TODO Auto-generated constructor stub
	}
	
	public int getElementType() {
		return REAL_EXPRESSION;
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
