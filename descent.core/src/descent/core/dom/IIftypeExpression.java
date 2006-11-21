package descent.core.dom;

/**
 * An is expression:
 * 
 * <pre>
 * is(type : specialization)
 * </pre>
 */
public interface IIftypeExpression extends IExpression {
	
	IName getIdentifier();
	
	IType getType();
	
	IType getSpecialization();
	
	IStrongType getStrongType();

}
