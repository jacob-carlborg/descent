package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IType;
import descent.core.dom.ITypeExpression;
import descent.core.domX.IASTVisitor;

public class TypeExp extends Expression implements ITypeExpression {

	private final Type t;

	public TypeExp(Type t) {
		this.t = t;
	}
	
	public IType getType() {
		return t;
	}
	
	public int getElementType() {
		return ElementTypes.TYPE_EXPRESSION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, t);
		}
		visitor.endVisit(this);
	}

}
