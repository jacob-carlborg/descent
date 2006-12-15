package descent.internal.core.dom;

import descent.core.dom.IExpression;
import descent.core.dom.ISliceType;
import descent.core.dom.IType;
import descent.core.domX.ASTVisitor;

public class TypeSlice extends Type implements ISliceType {

	private Expression from;
	private Expression to;

	public TypeSlice(Type t, Expression from, Expression to) {
		super(TY.Tslice, t);
		this.from = from;
		this.to = to;
	}
	
	public int getElementType() {
		return ElementTypes.SLICE_TYPE;
	}
	
	public IType getInnerType() {
		return next;
	}
	
	public IExpression getFrom() {
		return from;
	}
	
	public IExpression getTo() {
		return to;
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, next);
			acceptChild(visitor, from);
			acceptChild(visitor, to);
		}
		visitor.endVisit(this);
	}

}
