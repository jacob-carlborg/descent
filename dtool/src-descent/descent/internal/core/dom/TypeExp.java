package descent.internal.core.dom;

import descent.core.dom.IType;
import descent.core.dom.ITypeExpression;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;

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
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, t);
		}
		visitor.endVisit(this);
	}

}
