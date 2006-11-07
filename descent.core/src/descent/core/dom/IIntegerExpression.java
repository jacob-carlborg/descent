package descent.core.dom;

/**
 * An integer expression:
 */
public interface IIntegerExpression extends IExpression {
	
	/**
	 * Returns the integer.
	 */
	long getValue();

}
