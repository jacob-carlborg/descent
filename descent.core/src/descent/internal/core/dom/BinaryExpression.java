package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IBinaryExpression;
import descent.core.dom.IExpression;

public class BinaryExpression extends Expression implements IBinaryExpression {
	
	protected Operator operator;
	protected IExpression leftOperand;
	protected IExpression rightOperand;
	
	public BinaryExpression(Expression e1, Expression e2, Operator operator) {
		this(e1, e2);
		this.operator = operator;
	}
	
	public BinaryExpression(Expression e1, Expression e2) {
		this.leftOperand = e1;
		this.rightOperand = e2;
		
		this.start = e1.start;
		this.length = e2.start + e2.length - this.start;
	}
	
	public int getElementType() {
		return BINARY_EXPRESSION;
	}
	
	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	
	public Operator getOperator() {
		return operator;
	}
	
	public IExpression getLeftOperand() {
		return leftOperand;
	}
	
	public void setLefOperand(IExpression leftOperand) {
		this.leftOperand = leftOperand;
	}
	
	public IExpression getRightOperand() {
		return rightOperand;
	}
	
	public void setRightOperand(IExpression rightOperand) {
		this.rightOperand = rightOperand;
	}
	
	public void accept0(ElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, leftOperand);
			acceptChild(visitor, rightOperand);
		}
		visitor.endVisit(this);
	}

}
