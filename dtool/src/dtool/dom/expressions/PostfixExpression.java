package dtool.dom.expressions;

import dtool.dom.ast.ASTNeoVisitor;

public abstract class PostfixExpression extends Expression {
	
	public Expression exp;

	public PostfixExpression(Expression exp) {
		this.exp = exp;
	}
	
	@Override
	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
