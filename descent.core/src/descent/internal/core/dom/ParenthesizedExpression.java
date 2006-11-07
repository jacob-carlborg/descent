package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IParenthesizedExpression;

public class ParenthesizedExpression extends Expression implements IParenthesizedExpression {
	
	private Expression e;

	public ParenthesizedExpression(Expression e) {
		this.e = e;
	}
	
	public int getExpressionType() {
		return EXPRESSION_PARENTHESIZED;
	}

	public IExpression getExpression() {
		return e;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, e);
		}
		visitor.endVisit(this);
	}

}
