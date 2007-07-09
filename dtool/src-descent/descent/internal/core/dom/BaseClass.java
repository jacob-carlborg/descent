package descent.internal.core.dom;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.dom.IType;
import descent.core.domX.IASTVisitor;
import descent.core.domX.AbstractElement;

public class BaseClass extends AbstractElement {
	
	public int prot;
	public final Type type;

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
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, type);
		}
		visitor.endVisit(this);
	}

}
