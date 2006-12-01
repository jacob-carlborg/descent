package descent.core.dom;

/**
 * An array initializer:
 * 
 * <pre>
 * { len1 : init1, len2 : init2, len3 : init3, ... }
 * </pre>
 */
public interface IArrayInitializer extends IInitializer {
	
	/**
	 * Returns the lengths. Some may be <code>null</code>.
	 */
	IExpression[] getLengths();
	
	/**
	 * Returns the values. Some may be <code>null</code>.
	 */
	IInitializer[] getValues();

}
