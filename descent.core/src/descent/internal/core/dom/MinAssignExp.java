package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IBinaryExpression;
import descent.core.dom.IExpression;
import descent.core.dom.IUnaryExpression;

public class MinAssignExp extends BinaryExpression implements IUnaryExpression {

	private final boolean isUnary;

	public MinAssignExp(Expression e, Expression exp) {
		this(e, exp, false);
	}
	
	public MinAssignExp(Expression e, Expression exp, boolean isUnary) {
		super(e, exp);
		this.isUnary = isUnary;
	}
	
	public int getElementType() {
		return isUnary ? UNARY_EXPRESSION : BINARY_EXPRESSION;
	}
	
	public int getBinaryExpressionType() {
		return MIN_ASSIGN;
	}
	
	public int getUnaryExpressionType() {
		return PRE_DECREMENT;
	}
	
	public IExpression getInnerExpression() {
		return getLeftExpression();
	}
	
	@Override
	public void accept0(ElementVisitor visitor) {
		if (isUnary) {
			boolean children = visitor.visit((IUnaryExpression) this);
			if (children) {
				acceptChild(visitor, e1);
			}
			visitor.endVisit((IUnaryExpression) this);
		} else {
			boolean children = visitor.visit((IBinaryExpression) this);
			if (children) {
				acceptChild(visitor, e1);
				acceptChild(visitor, e2);
			}
			visitor.endVisit((IBinaryExpression) this);
		}
	}

}
