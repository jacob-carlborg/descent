package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IParenthesizedExpression;
import descent.core.domX.IASTVisitor;

public class ParenthesizedExpression extends Expression implements IParenthesizedExpression {
	
	public Expression e;

	public ParenthesizedExpression(Expression e) {
		this.e = e;
	}
	
	public int getElementType() {
		return ElementTypes.PARENTHESIZED_EXPRESSION;
	}

	public IExpression getExpression() {
		return e;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, e);
		}
		visitor.endVisit(this);
	}

}
