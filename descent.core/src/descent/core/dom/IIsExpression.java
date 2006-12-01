package descent.core.dom;

/**
 * An is expression:
 * 
 * <pre>
 * is(type : specialization)
 * </pre>
 */
public interface IIsExpression extends IExpression {
	
	ISimpleName getIdentifier();
	
	IType getType();
	
	IType getSpecialization();
	
	ITypeSpecialization getTypeSpecialization();

}
