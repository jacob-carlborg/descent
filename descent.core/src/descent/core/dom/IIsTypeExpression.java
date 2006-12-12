package descent.core.dom;

/**
 * An is expression:
 * 
 * <pre>
 * is(type : specialization)
 * </pre>
 */
public interface IIsTypeExpression extends IExpression {
	
	ISimpleName getName();
	
	IType getType();
	
	IType getSpecialization();

}
