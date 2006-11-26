package descent.internal.core.dom;

import java.math.BigInteger;

import descent.core.dom.IIntegerExpression;

public class IntegerExp extends Expression implements IIntegerExpression {
	
	private int expressionType;
	private BigInteger number;
	
	public IntegerExp(Loc loc, BigInteger number, Type type) {
		if (type == Type.tbool) {
			expressionType = number.compareTo(BigInteger.ZERO) == 0 ? FALSE_EXPRESSION : TRUE_EXPRESSION;
		} else {
			expressionType = INTEGER_EXPRESSION;
		}
		this.number = number;
	}
	
	public BigInteger getValue() {
		return number;
	}
	
	public int getElementType() {
		return expressionType;
	}
	
	@Override
	public String toString() {
		switch(expressionType) {
		case TRUE_EXPRESSION: return "true";
		case FALSE_EXPRESSION: return "false";
		default /* case EXPRESSION_INTEGER */: return String.valueOf(number);
		}
	}

}
