package descent.core.dom;

/**
 * A struct initializer:
 * 
 * <pre>
 * { id1 : init1, id2 : init2, id3 : init3, ... }
 * </pre>
 */
public interface IStructInitializer extends IInitializer {
	
	/**
	 * Returns the names. Some may be <code>null</code>.
	 */
	IName[] getNames();
	
	/**
	 * Returns the values assigned to the names. Some
	 * may be <code>null</code>.
	 */
	IInitializer[] getValues();

}
