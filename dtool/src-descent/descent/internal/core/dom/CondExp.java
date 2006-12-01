package descent.internal.core.dom;

import descent.core.dom.IConditionExpression;
import descent.core.dom.IExpression;
import descent.core.domX.ASTVisitor;

public class CondExp extends Expression implements IConditionExpression {
	
	private Expression cond;
	private Expression t;
	private Expression f;

	public CondExp(Expression cond, Expression t, Expression f) {
		this.cond = cond;
		this.t = t;
		this.f = f;
		this.startPos = cond.startPos;
		this.length = f.startPos + f.length - this.startPos;
	}
	
	public IExpression getCondition() {
		return cond;
	}
	
	public IExpression getTrue() {
		return t;
	}
	
	public IExpression getFalse() {
		return f;
	}
	
	public int getElementType() {
		return CONDITION_EXPRESSION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, cond);
			acceptChild(visitor, t);
			acceptChild(visitor, f);
		}
		visitor.endVisit(this);
	}

}
