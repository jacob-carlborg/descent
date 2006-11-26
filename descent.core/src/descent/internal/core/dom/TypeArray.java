package descent.internal.core.dom;

import descent.core.dom.IArrayType;
import descent.core.dom.IType;

public abstract class TypeArray extends Type implements IArrayType {
	
	public TypeArray(TY ty, Type next) {
		super(ty, next);
	}
	
	public IType getInnerType() {
		return next;
	}

}
