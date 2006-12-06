package descent.core.dom;

/**
 * A slice expression:
 * 
 * <pre>
 * expr[from .. to]
 * </pre>
 */
public interface ISliceExpression extends IExpression {
	
	/**
	 * Returns the expression to slice.
	 */
	IExpression getExpression();
	
	/**
	 * Returns where to begin in the slice.
	 */
	IExpression getFromExpression();
	
	/**
	 * Returns where to end the slice.
	 */
	IExpression getToExpression();

}
