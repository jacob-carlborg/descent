package descent.internal.core.dom;

import descent.core.dom.IType;
import descent.core.domX.ASTVisitor;

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
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, next);
		}
		visitor.endVisit(this);
	}
	
}
