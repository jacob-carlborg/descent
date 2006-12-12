package descent.core.dom;

import descent.internal.core.dom.IsTypeSpecializationExpression.TypeSpecialization;

/**
 * An is expression:
 * 
 * <pre>
 * is(type : specialization)
 * </pre>
 */
public interface IIsTypeSpecializationExpression extends IExpression {
	
	ISimpleName getName();
	
	IType getType();
	
	TypeSpecialization getSpecialization();

}
