package descent.internal.core.dom;

import java.math.BigInteger;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IIntegerExpression;

public class IntegerExp extends Expression implements IIntegerExpression {
	
	private int expressionType;
	private BigInteger number;
	
	public IntegerExp(BigInteger number, PrimitiveType.Code code) {
		if (code == PrimitiveType.Code.BOOL) {
			expressionType = number.compareTo(BigInteger.ZERO) == 0 ? FALSE_EXPRESSION : BOOLEAN_LITERAL;
		} else {
			expressionType = INTEGER_EXPRESSION;
		}
		this.number = number;
	}
	
	public BigInteger getValue() {
		return number;
	}
	
	public int getNodeType0() {
		return INTEGER_EXPRESSION;
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		visitor.visit((IIntegerExpression) this);
		visitor.endVisit((IIntegerExpression) this);
	}
	
	@Override
	public String toString() {
		return String.valueOf(number);
	}

}
