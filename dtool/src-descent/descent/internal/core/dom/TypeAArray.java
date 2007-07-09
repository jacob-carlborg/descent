package descent.internal.core.dom;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.dom.IType;
import descent.core.domX.IASTVisitor;

// asociative array
public class TypeAArray extends TypeArray implements IType {

	public Type index;

	public TypeAArray(Type t, Type index) {
		super(TY.Taarray, t);
		this.index = index;
	}
	
	public int getElementType() {
		return ElementTypes.ASSOCIATIVE_ARRAY_TYPE;
	}
	
	public IType getKeyType() {
		return index;
	}
	
	@Override
	public String toString() {
		return next.toString() + "[" + index.toString() + "]";
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, index);
		}
		visitor.endVisit(this);
	}

}
