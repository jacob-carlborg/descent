package descent.internal.core.dom;

import descent.core.dom.IExpression;
import descent.core.domX.ASTVisitor;

public class AddAssignExp extends BinaryExpression implements IExpression {
	
	private final boolean isUnary;

	public AddAssignExp(Expression e, Expression exp) {
		this(e, exp, false);
	}
	
	public AddAssignExp(Expression e, Expression exp, boolean isUnary) {
		super(e, exp);
		this.isUnary = isUnary;
	}
	
	public int getElementType() {
		return isUnary ? ElementTypes.UNARY_EXPRESSION : ElementTypes.BINARY_EXPRESSION;
	}
	
	public int getBinaryExpressionType() {
		return BinaryExpressionTypes.ADD_ASSIGN;
	}
	
	public int getUnaryExpressionType() {
		return UnaryExpression.IUnaryExpression2.PRE_INCREMENT;
	}
	
	public IExpression getInnerExpression() {
		return getLeftExpression();
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		if (isUnary) {
			boolean children = visitor.visit(this);
			if (children) {
				acceptChild(visitor, e1);
				acceptChild(visitor, e2);
			}
			visitor.endVisit((BinaryExpression) this);
		} else {
			boolean children = visitor.visit(this);
			if (children) {
				acceptChild(visitor, e1);
				acceptChild(visitor, e2);
			}
			visitor.endVisit((BinaryExpression) this);
		}
	}

}
