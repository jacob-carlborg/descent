package descent.internal.core.dom;

import descent.core.dom.IBaseClass;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IType;

public class BaseClass extends AbstractElement implements IBaseClass {
	
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
		return BASE_CLASS;
	}
	
	public IType getType() {
		return type;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, type);
		}
		visitor.endVisit(this);
	}

}
