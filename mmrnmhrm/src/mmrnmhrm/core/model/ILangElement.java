package mmrnmhrm.core.model;

import util.tree.IElement;

/** 
 * Work in progress. 
 */
public interface ILangElement extends IElement {
	
	//ILangElement[] NO_ELEMENTS = new ILangElement[0];

	/** Returns the name of this element. This is a handle-only method. */
	String getElementName();

	/** Returns the children of this element. */
	public ILangElement[] getChildren();

	// TODO: more methods?
}
