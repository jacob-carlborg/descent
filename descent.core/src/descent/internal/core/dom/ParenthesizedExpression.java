package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IParenthesizedExpression;

public class ParenthesizedExpression extends Expression implements IParenthesizedExpression {
	
	private Expression e;

	public ParenthesizedExpression(Expression e) {
		this.e = e;
	}
	
	public int getNodeType0() {
		return PARENTHESIZED_EXPRESSION;
	}

	public IExpression getExpression() {
		return e;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, e);
		}
		visitor.endVisit(this);
	}

}
