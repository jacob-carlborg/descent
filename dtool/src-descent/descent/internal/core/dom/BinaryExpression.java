package descent.internal.core.dom;

import descent.core.dom.IBinaryExpression;
import descent.core.dom.IExpression;
import descent.core.domX.ASTVisitor;

public abstract class BinaryExpression extends Expression implements IBinaryExpression {
	
	protected Expression e1;
	protected Expression e2;
	
	public BinaryExpression(Expression e1, Expression e2) {
		this.e1 = e1;
		this.e2 = e2;
		
		this.startPos = e1.startPos;
		this.length = e2.startPos + e2.length - this.startPos;
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
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, e1);
			acceptChild(visitor, e2);
		}
		visitor.endVisit(this);
	}

}
