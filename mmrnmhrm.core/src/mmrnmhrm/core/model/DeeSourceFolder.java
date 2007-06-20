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

	
	public void refreshElementChildren() throws CoreException {
		for(IResource resource : srcfolder.members()) {
			if(resource.getType() == IResource.FOLDER) {
				IFolder myfolder = (IFolder) resource;
				addChild(new PackageFragment(this, myfolder));
			}
		}
	}
	
	@Override
	public String toString() {
		return "["+srcfolder.getProjectRelativePath()+"]";
	}

}
