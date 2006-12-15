package descent.internal.core.dom;

import descent.core.domX.ASTVisitor;

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
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, getReturnType());
			acceptChildren(visitor, getArguments());
		}
		visitor.endVisit(this);
	}

}
