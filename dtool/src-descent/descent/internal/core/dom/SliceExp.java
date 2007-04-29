package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.ISliceExpression;
import descent.core.domX.IASTVisitor;

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
		return ElementTypes.SLICE_EXPRESSION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, e);
			TreeVisitor.acceptChild(visitor, from);
			TreeVisitor.acceptChild(visitor, to);
		}
		visitor.endVisit(this);
	}

}
