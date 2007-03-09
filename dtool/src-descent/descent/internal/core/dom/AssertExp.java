package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.domX.ASTVisitor;

public class AssertExp extends Expression {
	
	private Expression e;
	private Expression msg;

	public AssertExp(Expression e, Expression msg) {
		this.e = e;
		this.msg = msg;
	}
	
	public int getElementType() {
		return ElementTypes.ASSERT_EXPRESSION;
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
			TreeVisitor.acceptChild(visitor, e);
			TreeVisitor.acceptChild(visitor, msg);
		}
		visitor.endVisit(this);
	}

}
