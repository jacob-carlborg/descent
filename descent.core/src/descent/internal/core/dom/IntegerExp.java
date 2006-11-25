package descent.internal.core.dom;

import java.math.BigInteger;

import descent.core.dom.IIntegerExpression;

public class IntegerExp extends Expression implements IIntegerExpression {
	
	private int expressionType;
	private BigInteger number;
	
	public IntegerExp(Loc loc, BigInteger number, Type type) {
		if (type == Type.tbool) {
			expressionType = number.compareTo(BigInteger.ZERO) == 0 ? EXPRESSION_FALSE : EXPRESSION_TRUE;
		} else {
			expressionType = EXPRESSION_INTEGER;
		}
		this.number = number;
	}
	
	public BigInteger getValue() {
		return number;
	}
	
	public int getExpressionType() {
		return expressionType;
	}
	
	@Override
	public String toString() {
		switch(expressionType) {
		case EXPRESSION_TRUE: return "true";
		case EXPRESSION_FALSE: return "false";
		default /* case EXPRESSION_INTEGER */: return String.valueOf(number);
		}
	}

}
