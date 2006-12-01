package descent.internal.core.dom;

import descent.core.dom.IDeleteExpression;
import descent.core.dom.ASTVisitor;
import descent.core.dom.IExpression;

public class DeleteExp extends Expression implements IDeleteExpression {

	private final Expression e;

	public DeleteExp(Expression e) {
		this.e = e;
	}
	
	public IExpression getExpression() {
		return e;
	}
	
	public int getElementType() {
		return DELETE_EXPRESSION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, e);
		}
		visitor.endVisit(this);
	}

}
