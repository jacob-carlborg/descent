package descent.core.dom;

/**
 * A selective import.
 * 
 * @see IImport
 */
public interface ISelectiveImport extends IElement {
	
	/**
	 * Returns the name of this selective import.
	 */
	ISimpleName getName();
	
	/**
	 * Returns the alias of this selective import.
	 */
	ISimpleName getAlias();

}
