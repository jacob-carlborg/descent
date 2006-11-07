package descent.internal.core.dom;

import descent.core.dom.ICastExpression;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IType;

public class CastExp extends Expression implements ICastExpression {

	private final Expression e;
	private final Type t;

	public CastExp(Loc loc, Expression e, Type t) {
		this.e = e;
		this.t = t;
	}
	
	public IType getType() {
		return t;
	}
	
	public IExpression getExpression() {
		return e;
	}
	
	public int getExpressionType() {
		return EXPRESSION_CAST;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, t);
			acceptChild(visitor, e);
		}
		visitor.endVisit(this);
	}

}
