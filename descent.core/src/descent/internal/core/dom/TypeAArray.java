package descent.internal.core.dom;

import descent.core.dom.IAssociativeArrayType;
import descent.core.dom.ASTVisitor;
import descent.core.dom.IType;

// asociative array
public class TypeAArray extends TypeArray implements IAssociativeArrayType {

	public Type index;

	public TypeAArray(Type t, Type index) {
		super(TY.Taarray, t);
		this.index = index;
	}
	
	public int getElementType() {
		return ASSOCIATIVE_ARRAY_TYPE;
	}
	
	public IType getKeyType() {
		return index;
	}
	
	@Override
	public String toString() {
		return next.toString() + "[" + index.toString() + "]";
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, index);
		}
		visitor.endVisit(this);
	}

}
