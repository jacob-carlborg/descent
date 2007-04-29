package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IType;
import descent.core.domX.IASTVisitor;

public class TypeDArray extends TypeArray implements IType {

	public TypeDArray(Type t) {
		super(TY.Tarray, t);
	}
	
	public int getElementType() {
		return ElementTypes.DYNAMIC_ARRAY_TYPE;
	}
	
	@Override
	public String toString() {
		return next.toString() + "[]";
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, next);
		}
		visitor.endVisit(this);
	}
	
}
