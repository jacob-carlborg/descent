package descent.internal.core.dom;

import descent.core.dom.IArgument;
import descent.core.dom.IDelegateType;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IPointerType;
import descent.core.dom.IType;

public class TypePointer extends Type implements IPointerType, IDelegateType {
	
	public TypePointer(Type t) {
		super(TY.Tpointer, t);
	}
	
	public int getElementType() {
		return next instanceof TypeFunction ? POINTER_TO_FUNCTION_TYPE : POINTER_TYPE;
	}
	
	public IType getInnerType() {
		return next;
	}
	
	public IType getReturnType() {
		return ((TypeFunction) next).getReturnType();
	}
	
	public IArgument[] getArguments() {
		return ((TypeFunction) next).getArguments();
	}
	
	@Override
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			if (next instanceof TypeFunction) {
				acceptChild(visitor, getReturnType());
				acceptChildren(visitor, getArguments());
			} else {
				acceptChild(visitor, next);
			}
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toString() {
		return next.toString() + "*";
	}

}
