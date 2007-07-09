package descent.internal.core.dom;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.dom.IType;
import descent.core.domX.IASTVisitor;

public class TypePointer extends Type {
	
	public TypePointer(Type t) {
		super(TY.Tpointer, t);
	}
	
	public int getElementType() {
		return ElementTypes.POINTER_TYPE;
	}
	
	public IType getInnerType() {
		return next;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children;
		children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, next);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toString() {
		return next.toString() + "*";
	}

}
