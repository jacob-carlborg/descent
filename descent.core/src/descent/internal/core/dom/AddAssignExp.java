package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IUnaryExpression;

public class AddAssignExp extends BinaryExpression implements IUnaryExpression {
	
	private final boolean isUnary;

	public AddAssignExp(Loc loc, Expression e, Expression exp) {
		this(loc, e, exp, false);
	}
	
	public AddAssignExp(Loc loc, Expression e, Expression exp, boolean isUnary) {
		super(e, exp);
		this.isUnary = isUnary;
	}
	
	public int getElementType() {
		return isUnary ? UNARY_EXPRESSION : BINARY_EXPRESSION;
	}
	
	public int getBinaryExpressionType() {
		return ADD_ASSIGN;
	}
	
	public int getUnaryExpressionType() {
		return PRE_INCREMENT;
	}
	
	public IExpression getInnerExpression() {
		return getLeftExpression();
	}
	
	@Override
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, e1);
			if (!isUnary) {
				acceptChild(visitor, e2);
			}
		}
		visitor.endVisit(this);
	}

}
