package descent.internal.core.dom;

import descent.core.dom.IExpression;
import descent.core.domX.ASTVisitor;

public class DeleteExp extends Expression {

	private final Expression e;

	public DeleteExp(Expression e) {
		this.e = e;
	}
	
	public IExpression getExpression() {
		return e;
	}
	
	public int getElementType() {
		return ElementTypes.DELETE_EXPRESSION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, e);
		}
		visitor.endVisit(this);
	}

}
