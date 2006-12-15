package descent.internal.core.dom;

import descent.core.dom.IType;
import descent.core.domX.ASTVisitor;
import descent.core.domX.AbstractElement;

public class BaseClass extends AbstractElement {
	
	private int prot;
	private final Type type;

	public BaseClass(Type type, PROT protection) {
		this.type = type;
		this.prot = protection.getModifiers();
	}
	
	public int getModifiers() {
		return prot;
	}

	public int getElementType() {
		return ElementTypes.BASE_CLASS;
	}
	
	public IType getType() {
		return type;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, type);
		}
		visitor.endVisit(this);
	}

}
