package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IUnaryExpression;

public class UnaryExpression extends Expression implements IUnaryExpression {
	
	private Operator operator;
	private Expression exp;
	
	public UnaryExpression(Expression exp, Operator operator) {
		this.exp = exp;
		this.operator = operator;
	}

	public UnaryExpression(Expression exp) {
		this.exp = exp;
	}
	
	public IExpression getInnerExpression() {
		return exp;
	}
	
	public int getNodeType0() {
		return UNARY_EXPRESSION;
	}
	
	public Operator getOperator() {
		return operator;
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
