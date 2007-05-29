package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.domX.IASTVisitor;

public class DeleteExp extends Expression {

	public final Expression e;

	public DeleteExp(Expression e) {
		this.e = e;
	}
	
	public IExpression getExpression() {
		return e;
	}
	
	public int getElementType() {
		return ElementTypes.DELETE_EXPRESSION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, e);
		}
		visitor.endVisit(this);
	}

}
