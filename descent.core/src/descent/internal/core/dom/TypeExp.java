package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IType;
import descent.core.dom.ITypeExpression;

public class TypeExp extends Expression implements ITypeExpression {

	private final Type t;

	public TypeExp(Loc loc, Type t) {
		this.t = t;
	}
	
	public IType getType() {
		return t;
	}
	
	public int getElementType() {
		return TYPE_EXPRESSION;
	}
	
	public void accept0(ElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, t);
		}
		visitor.endVisit(this);
	}

}
