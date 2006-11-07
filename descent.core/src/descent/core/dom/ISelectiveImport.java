package descent.core.dom;

/**
 * A selective import.
 * 
 * @see IImport
 */
public interface ISelectiveImport extends IDElement {
	
	/**
	 * Returns the name of this selective import.
	 */
	IName getName();
	
	/**
	 * Returns the alias of this selective import.
	 */
	IName getAlias();

}
