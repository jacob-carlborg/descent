package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IUnaryExpression;

public abstract class UnaryExpression extends Expression implements IUnaryExpression {
	
	private final Expression exp;

	public UnaryExpression(Expression exp) {
		this.exp = exp;
	}
	
	public IExpression getInnerExpression() {
		return exp;
	}
	
	public int getExpressionType() {
		return EXPRESSION_UNARY;
	}
	
	@Override
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
