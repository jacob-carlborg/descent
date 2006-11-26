package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IDynamicArrayType;

public class TypeDArray extends TypeArray implements IDynamicArrayType {

	public TypeDArray(Type t) {
		super(TY.Tarray, t);
	}
	
	public int getElementType() {
		return DYNAMIC_ARRAY_TYPE;
	}
	
	@Override
	public String toString() {
		return next.toString() + "[]";
	}
	
	@Override
	public void accept0(ElementVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
}
