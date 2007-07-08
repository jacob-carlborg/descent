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
		addChild(new PackageFragment(this, this.folder));
		for(IResource resource : folder.members()) {
			if(resource.getType() == IResource.FOLDER) {
				IFolder myfolder = (IFolder) resource;
				addChild(new PackageFragment(this, myfolder));
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
