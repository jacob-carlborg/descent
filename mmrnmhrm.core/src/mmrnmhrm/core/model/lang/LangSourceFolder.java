package mmrnmhrm.core.model.lang;


import mmrnmhrm.core.model.DeePackageFragment;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public abstract class LangSourceFolder extends LangContainerElement implements ILangSourceRoot {

	public IFolder folder;

	public LangSourceFolder(LangContainerElement parent, IFolder folder) {
		super(parent);
		this.folder = folder;
	}

	public String getElementName() {
		return this.folder.getProjectRelativePath().toString();
	}

	public String toString() {
		return getElementName();
	}

	public int getElementType() {
		return ELangElementTypes.SOURCEFOLDER;
	}
	
	public IFolder getUnderlyingResource() {
		return folder;
	}
	
	public IPath getProjectRelativePath() {
		return folder.getProjectRelativePath();
	}
	
	/* -------------- Structure  -------------- */

	
	protected void createElementInfo() throws CoreException {
		clearChildren();
		if(getUnderlyingResource().exists() == false)
			return;
		// add the empty package
		addChild(new DeePackageFragment(this, this.folder));
		internalCreatePackageFragments(folder);
	}
	
	protected void disposeElementInfo() {
		setChildren(newChildrenArray(0)); // reset children
	}
	
	/** Create package fragments found in the given parent folder. */
	private void internalCreatePackageFragments(IFolder parent) throws CoreException {
		for(IResource resource : parent.members()) {
			if(resource.getType() == IResource.FOLDER) {
				IFolder folder = (IFolder) resource;
				if(isValidPackageFragmentFolder(folder)) {
					addChild(new DeePackageFragment(this, folder));
					internalCreatePackageFragments(folder);
				} else {
					// Keep track of non-lang element resources?
				}
			}
		}
	}
	
	protected abstract boolean isValidPackageFragmentFolder(IFolder folder);

	/** {@inheritDoc} */
	public LangPackageFragment[] getPackageFragments() throws CoreException {
		getElementInfo();
		return (LangPackageFragment[]) getChildren();
	}

}