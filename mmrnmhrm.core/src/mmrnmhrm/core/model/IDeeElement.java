package mmrnmhrm.core.model;

import org.eclipse.core.runtime.CoreException;

import mmrnmhrm.core.model.lang.ILangElement;


public interface IDeeElement extends ILangElement{

	/** Updates the lang model according to the underlying filesystem data. */
	void refreshElementChildren() throws CoreException;

}
