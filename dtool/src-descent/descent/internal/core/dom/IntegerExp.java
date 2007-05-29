package descent.internal.core.dom;

import java.math.BigInteger;

import descent.core.dom.IFalseExpression;
import descent.core.dom.IIntegerExpression;
import descent.core.dom.ITrueExpression;
import descent.core.domX.IASTVisitor;

public class IntegerExp extends Expression implements IIntegerExpression, IFalseExpression, ITrueExpression {
	
	public int expressionType;
	public BigInteger number;
	
	public IntegerExp(BigInteger number, Type type) {
		if (type == Type.tbool) {
			expressionType = number.compareTo(BigInteger.ZERO) == 0 ? ElementTypes.FALSE_EXPRESSION : ElementTypes.TRUE_EXPRESSION;
		} else {
			expressionType = ElementTypes.INTEGER_EXPRESSION;
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
	public void accept0(IASTVisitor visitor) {
		switch(expressionType) {
		case ElementTypes.TRUE_EXPRESSION:
			visitor.visit((ITrueExpression) this);
			visitor.endVisit((ITrueExpression) this);
			break;
		case ElementTypes.FALSE_EXPRESSION:
			visitor.visit((IFalseExpression) this);
			visitor.endVisit((IFalseExpression) this);
			break;
		case ElementTypes.INTEGER_EXPRESSION:
			visitor.visit((IIntegerExpression) this);
			visitor.endVisit((IIntegerExpression) this);
			break;
		}
	}
	
	@Override
	public String toString() {
		switch(expressionType) {
		case ElementTypes.TRUE_EXPRESSION: return "true";
		case ElementTypes.FALSE_EXPRESSION: return "false";
		case ElementTypes.INTEGER_EXPRESSION: return String.valueOf(number);
		}
		throw new IllegalStateException("Can't happen");
	}

}
