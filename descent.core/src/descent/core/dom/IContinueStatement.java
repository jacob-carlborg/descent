package descent.core.dom;

/**
 * A continue statement:
 * 
 * <pre>
 * continue label;
 * </pre>
 * 
 * where "label" is optional.
 */
public interface IContinueStatement extends IStatement {
	
	/**
	 * Returns the label to continue to, if any, or <code>null</code>.
	 */
	ISimpleName getLabel();

}
