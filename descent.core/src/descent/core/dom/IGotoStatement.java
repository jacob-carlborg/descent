package descent.core.dom;

/**
 * A goto statement:
 * 
 * <pre>
 * goto label;
 * </pre>
 */
public interface IGotoStatement extends IStatement {
	
	/**
	 * Returns the label where to go to.
	 */
	ISimpleName getLabel();

}
