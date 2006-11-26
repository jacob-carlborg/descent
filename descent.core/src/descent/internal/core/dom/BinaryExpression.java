package descent.internal.core.dom;

import descent.core.dom.IBinaryExpression;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;

public abstract class BinaryExpression extends Expression implements IBinaryExpression {
	
	protected Expression e1;
	protected Expression e2;
	
	public BinaryExpression(Expression e1, Expression e2) {
		this.e1 = e1;
		this.e2 = e2;
		
		this.start = e1.start;
		this.length = e2.start + e2.length - this.start;
	}
	
	public int getElementType() {
		return BINARY_EXPRESSION;
	}
	
	public IExpression getLeftExpression() {
		return e1;
	}
	
	public IExpression getRightExpression() {
		return e2;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, e1);
			acceptChild(visitor, e2);
		}
		visitor.endVisit(this);
	}

}
