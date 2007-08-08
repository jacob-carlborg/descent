package mmrnmhrm.core.model.lang;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;

import melnorme.miscutil.tree.IElement;

/** 
 * Work in progress. 
 */
public interface ILangElement extends IElement, IAdaptable {
	
	ILangElement[] NO_LANGELEMENTS = new ILangElement[0];

	/** Returns the name of this element. This is a handle-only method. */
	String getElementName();

	/** Returns the smallest underlying resource that contains
	 * this element, or <code>null</code> if this element is not contained
	 * in a resource.
	 */
	IResource getUnderlyingResource(); 
	
	/** Updates this element and all others beneath it. */
	void updateElementRecursive() throws CoreException ;
	

}
