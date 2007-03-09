package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IPointerType;
import descent.core.dom.IType;
import descent.core.domX.ASTVisitor;

public class TypePointer extends Type implements IPointerType {
	
	public TypePointer(Type t) {
		super(TY.Tpointer, t);
	}
	
	public int getElementType() {
		return next instanceof TypeFunction ? ElementTypes.POINTER_TO_FUNCTION_TYPE : ElementTypes.POINTER_TYPE;
	}
	
	public IType getInnerType() {
		return next;
	}
	
	public Type getReturnType() {
		return ((TypeFunction) next).getReturnType();
	}
	
	public Argument[] getArguments() {
		return ((TypeFunction) next).getArguments();
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		boolean children;
		switch(getElementType()) {
		case ElementTypes.POINTER_TO_FUNCTION_TYPE:
			children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChild(visitor, getReturnType());
				TreeVisitor.acceptChildren(visitor, getArguments());
			}
			visitor.endVisit(this);
			break;
		case ElementTypes.POINTER_TYPE:
			children = visitor.visit((IPointerType) this);
			if (children) {
				TreeVisitor.acceptChild(visitor, next);
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
