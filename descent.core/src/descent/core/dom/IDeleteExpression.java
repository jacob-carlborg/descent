package descent.core.dom;

/**
 * A delete expression:
 * 
 * <pre>
 * delete expr
 * </pre>
 *
 */
public interface IDeleteExpression extends IExpression {
	
	/**
	 * Returns the expression to delete.
	 */
	IExpression getExpression();

}
