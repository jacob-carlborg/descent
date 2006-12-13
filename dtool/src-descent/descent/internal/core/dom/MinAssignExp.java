package descent.internal.core.dom;

import descent.core.dom.IExpression;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;
import descent.internal.core.dom.BinaryExpression.BinaryExpressionTypes;
import descent.internal.core.dom.UnaryExpression.IUnaryExpression2;

public class MinAssignExp extends BinaryExpression {

	private final boolean isUnary;

	public MinAssignExp(Expression e, Expression exp) {
		this(e, exp, false);
	}
	
	public MinAssignExp(Expression e, Expression exp, boolean isUnary) {
		super(e, exp);
		this.isUnary = isUnary;
	}
	
	public int getElementType() {
		return isUnary ? ElementTypes.UNARY_EXPRESSION : ElementTypes.BINARY_EXPRESSION;
	}
	
	public int getBinaryExpressionType() {
		return BinaryExpressionTypes.MIN_ASSIGN;
	}
	
	public int getUnaryExpressionType() {
		return UnaryExpression.IUnaryExpression2.PRE_DECREMENT;
	}
	
	public IExpression getInnerExpression() {
		return getLeftExpression();
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		if (isUnary) {
			boolean children = visitor.visit( this);
			if (children) {
				acceptChild(visitor, e1);
				acceptChild(visitor, e2);
			}
			visitor.endVisit((BinaryExpression) this);
		} else {
			boolean children = visitor.visit( this);
			if (children) {
				acceptChild(visitor, e1);
				acceptChild(visitor, e2);
			}
			visitor.endVisit((BinaryExpression) this);
		}
	}

}
