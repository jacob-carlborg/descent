package mmrnmhrm.core.model.lang;


import melnorme.miscutil.ArrayUtil;
import mmrnmhrm.core.model.DeePackageFragment;
import mmrnmhrm.core.model.DeeProject;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;

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
		
		createSrcFolderEntry(getProjectFragment());
		
		// add the empty package
		addChild(new DeePackageFragment(this, this.folder));
		internalCreatePackageFragments(folder);
	}

	public IProjectFragment getProjectFragment() {
		DeeProject deeProj = (DeeProject) parent;
		return deeProj.dltkProj.getProjectFragment(folder);
	}

	protected static void createSrcFolderEntry(IProjectFragment fragment)
			throws CoreException {
		if(fragment == null || !fragment.exists()) {
			IScriptProject dltkProj = fragment.getScriptProject();
			IBuildpathEntry[] bpentries = dltkProj.getRawBuildpath();
			IBuildpathEntry entry = DLTKCore.newSourceEntry(fragment.getPath());
			dltkProj.setRawBuildpath(ArrayUtil.concat(bpentries, entry), null);
		}
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