package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IType;
import descent.core.dom.ITypeidExpression;
import descent.core.domX.IASTVisitor;

public class TypeidExp extends Expression implements ITypeidExpression {

	private final Type type;

	public TypeidExp(Type type) {
		this.type = type;
	}
	
	public IType getType() {
		return type;
	}
	
	public int getElementType() {
		return ElementTypes.TYPEID_EXPRESSION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, type);
		}
		visitor.endVisit(this);
	}

}
