package mmrnmhrm.core.model;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import mmrnmhrm.core.model.lang.ILangElement;


public interface IDeeElement extends ILangElement{

	/** Updates the lang model according to the underlying filesystem data. */
	void updateElementRecursive() throws CoreException;

	/** Update the lang element. */
	void updateElement() throws CoreException;
	
	/** Gets the resource for this Dee element. */
	IResource getUnderlyingResource();
	
}
