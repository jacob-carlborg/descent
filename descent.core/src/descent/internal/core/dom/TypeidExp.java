package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IType;
import descent.core.dom.ITypeidExpression;

public class TypeidExp extends Expression implements ITypeidExpression {

	private final Type type;

	public TypeidExp(Loc loc, Type type) {
		this.type = type;
	}
	
	public IType getType() {
		return type;
	}
	
	public int getElementType() {
		return TYPEID_EXPRESSION;
	}
	
	public void accept0(ElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, type);
		}
		visitor.endVisit(this);
	}

}
