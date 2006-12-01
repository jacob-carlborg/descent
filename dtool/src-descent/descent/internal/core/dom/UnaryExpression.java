package descent.internal.core.dom;

import descent.core.dom.IExpression;
import descent.core.dom.IUnaryExpression;
import descent.core.domX.ASTVisitor;

public abstract class UnaryExpression extends Expression implements IUnaryExpression {
	
	private final Expression exp;

	public UnaryExpression(Expression exp) {
		this.exp = exp;
	}
	
	public IExpression getInnerExpression() {
		return exp;
	}
	
	public int getElementType() {
		return UNARY_EXPRESSION;
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
