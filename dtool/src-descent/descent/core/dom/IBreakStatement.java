package descent.core.dom;

/**
 * A break statement:
 * 
 * <pre>
 * break label;
 * </pre>
 * 
 * where "label" is optional.
 */
public interface IBreakStatement extends IDescentStatement {
	
	/**
	 * Returns the label, if any, or <code>null</code>.
	 */
	IName getLabel();

}
