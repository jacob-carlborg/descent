package descent.internal.core.dom;

import descent.core.dom.IAssertExpression;
import descent.core.dom.ASTVisitor;
import descent.core.dom.IExpression;

public class AssertExp extends Expression implements IAssertExpression {
	
	private Expression e;
	private Expression msg;

	public AssertExp(Expression e, Expression msg) {
		this.e = e;
		this.msg = msg;
	}
	
	public int getElementType() {
		return ASSERT_EXPRESSION;
	}
	
	public IExpression getExpression() {
		return e;
	}
	
	public IExpression getMessage() {
		return msg;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, e);
			acceptChild(visitor, msg);
		}
		visitor.endVisit(this);
	}

}
