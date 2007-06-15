package descent.core.dom;

/**
 * A goto statement:
 * 
 * <pre>
 * goto label;
 * </pre>
 */
public interface IGotoStatement extends IDescentStatement {
	
	/**
	 * Returns the label where to go to.
	 */
	IName getLabel();

}
