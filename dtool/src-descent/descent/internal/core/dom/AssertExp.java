package descent.internal.core.dom;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

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
	
	public Expression getExpression() {
		return e;
	}
	
	public Expression getMessage() {
		return msg;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, e);
			TreeVisitor.acceptChild(visitor, msg);
		}
		visitor.endVisit(this);
	}

}
