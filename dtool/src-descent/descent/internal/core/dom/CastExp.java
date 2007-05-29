package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.ICastExpression;
import descent.core.dom.IExpression;
import descent.core.dom.IType;
import descent.core.domX.IASTVisitor;

public class CastExp extends Expression implements ICastExpression {

	public final Expression e;
	public final Type t;

	public CastExp(Expression e, Type t) {
		this.e = e;
		this.t = t;
	}
	
	public IType getType() {
		return t;
	}
	
	public IExpression getExpression() {
		return e;
	}
	
	public int getElementType() {
		return ElementTypes.CAST_EXPRESSION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, t);
			TreeVisitor.acceptChild(visitor, e);
		}
		visitor.endVisit(this);
	}

}
