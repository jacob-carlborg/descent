package descent.internal.core.dom;

import descent.core.dom.IDeleteExpression;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;

public class DeleteExp extends Expression implements IDeleteExpression {

	private final Expression e;

	public DeleteExp(Loc loc, Expression e) {
		this.e = e;
	}
	
	public IExpression getExpression() {
		return e;
	}
	
	public int getExpressionType() {
		return EXPRESSION_DELETE;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, e);
		}
		visitor.endVisit(this);
	}

}
