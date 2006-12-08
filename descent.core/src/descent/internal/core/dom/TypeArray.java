package descent.internal.core.dom;

import descent.core.dom.IArrayType;
import descent.core.dom.IType;

public abstract class TypeArray extends DmdType implements IArrayType {
	
	public TypeArray(TY ty, DmdType next) {
		super(ty, next);
	}
	
	public IType getComponentType() {
		return next;
	}

}
