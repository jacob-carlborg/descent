package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IConditionExpression;
import descent.core.dom.IExpression;
import descent.core.domX.IASTVisitor;

public class CondExp extends Expression implements IConditionExpression {
	
	public Expression cond;
	public Expression t;
	public Expression f;

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
		return ElementTypes.CONDITION_EXPRESSION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, cond);
			TreeVisitor.acceptChild(visitor, t);
			TreeVisitor.acceptChild(visitor, f);
		}
		visitor.endVisit(this);
	}

}
