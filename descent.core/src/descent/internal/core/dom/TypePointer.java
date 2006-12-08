package descent.internal.core.dom;

import descent.core.dom.IArgument;
import descent.core.dom.IDelegateType;
import descent.core.dom.ASTVisitor;
import descent.core.dom.IPointerType;
import descent.core.dom.IType;

public class TypePointer extends DmdType implements IPointerType, IDelegateType {
	
	public TypePointer(DmdType t) {
		super(TY.Tpointer, t);
	}
	
	public int getNodeType0() {
		return next instanceof TypeFunction ? POINTER_TO_FUNCTION_TYPE : POINTER_TYPE;
	}
	
	public IType getInnerType() {
		return next;
	}
	
	public IType getReturnType() {
		return ((TypeFunction) next).getReturnType();
	}
	
	public IArgument[] getArguments() {
		return ((TypeFunction) next).getArguments().toArray(new IArgument[((TypeFunction) next).getArguments().size()]);
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		boolean children;
		switch(getNodeType0()) {
		case POINTER_TO_FUNCTION_TYPE:
			children = visitor.visit((IDelegateType) this);
			if (children) {
				acceptChild(visitor, getReturnType());
				acceptChildren(visitor, getArguments());
			}
			visitor.endVisit((IDelegateType) this);
			break;
		case POINTER_TYPE:
			children = visitor.visit((IPointerType) this);
			if (children) {
				acceptChild(visitor, next);
			}
			visitor.endVisit((IPointerType) this);
			break;
		}		
	}
	
	@Override
	public String toString() {
		return next.toString() + "*";
	}

}
