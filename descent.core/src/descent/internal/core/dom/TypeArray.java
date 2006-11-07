package descent.internal.core.dom;

import descent.core.dom.IArrayType;
import descent.core.dom.IType;

public class TypeArray extends Type implements IArrayType {
	
	public TypeArray(TY ty, Type next) {
		super(ty, next);
	}
	
	public IType getInnerType() {
		return next;
	}
	
	@Override
	public int getTypeType() {
		return TYPE_ARRAY;
	}
	
	public int getArrayTypeType() {
		return DYNAMIC_ARRAY;
	}

}
