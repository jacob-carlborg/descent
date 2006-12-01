package descent.internal.core.dom;

import java.math.BigInteger;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IFalseExpression;
import descent.core.dom.IIntegerExpression;
import descent.core.dom.ITrueExpression;

public class IntegerExp extends Expression implements IIntegerExpression, IFalseExpression, ITrueExpression {
	
	private int expressionType;
	private BigInteger number;
	
	public IntegerExp(BigInteger number, Type type) {
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
	public void accept0(ASTVisitor visitor) {
		switch(expressionType) {
		case TRUE_EXPRESSION:
			visitor.visit((ITrueExpression) this);
			visitor.endVisit((ITrueExpression) this);
			break;
		case FALSE_EXPRESSION:
			visitor.visit((IFalseExpression) this);
			visitor.endVisit((IFalseExpression) this);
			break;
		case INTEGER_EXPRESSION:
			visitor.visit((IIntegerExpression) this);
			visitor.endVisit((IIntegerExpression) this);
			break;
		}
	}
	
	@Override
	public String toString() {
		switch(expressionType) {
		case TRUE_EXPRESSION: return "true";
		case FALSE_EXPRESSION: return "false";
		case INTEGER_EXPRESSION: return String.valueOf(number);
		}
		throw new IllegalStateException("Can't happen");
	}

}
