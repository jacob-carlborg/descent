package descent.internal.core.dom;

import descent.core.dom.IArgument;
import descent.core.dom.IDelegateType;
import descent.core.dom.ASTVisitor;
import descent.core.dom.IType;

public class TypeDelegate extends Type implements IDelegateType {

	public TypeDelegate(Type t) {
		super(TY.Tdelegate, t);
	}
	
	public IType getReturnType() {
		return ((TypeFunction) next).getReturnType();
	}
	
	public IArgument[] getArguments() {
		return ((TypeFunction) next).getArguments().toArray(new IArgument[((TypeFunction) next).getArguments().size()]);
	}
	
	public int getNodeType0() {
		return DELEGATE_TYPE;
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
