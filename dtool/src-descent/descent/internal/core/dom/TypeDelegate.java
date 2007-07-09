package descent.internal.core.dom;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class TypeDelegate extends Type {

	public TypeDelegate(Type t) {
		super(TY.Tdelegate, t);
	}
	
	public Type getReturnType() {
		return ((TypeFunction) next).getReturnType();
	}
	
	public Argument[] getArguments() {
		return ((TypeFunction) next).getArguments();
	}
	
	public int getElementType() {
		return ElementTypes.DELEGATE_TYPE;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, getReturnType());
			TreeVisitor.acceptChildren(visitor, getArguments());
		}
		visitor.endVisit(this);
	}

}
