package descent.core.dom;

import descent.internal.core.dom.PrimitiveType.Code;

/**
 * A basic (primitive) type.
 */
public interface IBasicType extends IType {
	
	Code getPrimitiveTypeCode();

}
