package descent.core.dom;

import java.math.BigInteger;

/**
 * An integer expression:
 */
public interface IIntegerExpression extends IExpression {
	
	/**
	 * Returns the integer.
	 */
	BigInteger getValue();

}
