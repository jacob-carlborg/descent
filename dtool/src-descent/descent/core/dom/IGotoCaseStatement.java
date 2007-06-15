package descent.core.dom;

/**
 * A goto case statement:
 * 
 * <pre>
 * goto case;
 * </pre>
 */
public interface IGotoCaseStatement extends IDescentStatement {
	
	/**
	 * Returns the case to go.
	 */
	IExpression getCase();

}
