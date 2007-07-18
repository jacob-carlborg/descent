package mmrnmhrm.core.model;

import mmrnmhrm.core.model.lang.ILangElement;
import mmrnmhrm.core.model.lang.LangSourceFolder;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class DeeSourceFolder extends LangSourceFolder implements IDeeSourceRoot {
	
	public DeeSourceFolder(IFolder folder, DeeProject parent) {
		super(parent, folder);
	}
	
	@Override
	public ILangElement[] newChildrenArray(int size) {
		return new PackageFragment[size]; // no children for now
	}

	@Override
	public String toString() {
		return folder.getProjectRelativePath().toString();
	}

	public IFolder getUnderlyingResource() {
		return folder;
	}
	
	public PackageFragment[] getPackageFragments() {
		return (PackageFragment[]) getChildren();
	}
	
	public void updateElement() throws CoreException {
		// add the empty package
		clearChildren();
		addChild(new PackageFragment(this, this.folder));
		addPackageFragments(folder);
	}
	
	/** Add package fragments found in the given parentfolder. */
	private void addPackageFragments(IFolder parent) throws CoreException {
		for(IResource resource : parent.members()) {
			if(resource.getType() == IResource.FOLDER) {
				IFolder folder = (IFolder) resource;
				addChild(new PackageFragment(this, folder));
				addPackageFragments(folder);
			}
		}
	}

	public void updateElementRecursive() throws CoreException {
		updateElement();
		for(PackageFragment element : getPackageFragments()) {
			element.updateElementRecursive();
		}
	}
	
}
