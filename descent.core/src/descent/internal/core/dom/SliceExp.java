package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.ISliceExpression;

public class SliceExp extends Expression implements ISliceExpression {

	private final Expression e;
	private final Expression from;
	private final Expression to;

	public SliceExp(Expression e, Expression from, Expression to) {
		this.e = e;
		this.from = from;
		this.to = to;
	}
	
	public IExpression getExpression() {
		return e;
	}
	
	public IExpression getFrom() {
		return from;
	}
	
	public IExpression getTo() {
		return to;
	}
	
	public int getElementType() {
		return SLICE_EXPRESSION;
	}
	
	public void accept0(ElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, e);
			acceptChild(visitor, from);
			acceptChild(visitor, to);
		}
		visitor.endVisit(this);
	}

}
