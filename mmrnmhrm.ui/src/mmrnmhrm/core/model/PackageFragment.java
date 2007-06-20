package mmrnmhrm.core.model;

import mmrnmhrm.core.model.lang.ELangElementTypes;
import mmrnmhrm.core.model.lang.ILangElement;
import mmrnmhrm.core.model.lang.LangElement;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class PackageFragment extends LangElement {

	IFolder packageFolder;
	
	public PackageFragment(LangElement parent, IFolder myfolder) throws CoreException {
		super(parent);
		packageFolder = myfolder;
		refreshElementChildren();
	}

	private void refreshElementChildren() throws CoreException {
		for(IResource resource : packageFolder.members()) {
			if(resource.getType() == IResource.FILE) {
				IFile myfolder = (IFile) resource;
				addChild(new CompilationUnit(this, myfolder));
			}
		}
	}

	public String getElementName() {
		return this.packageFolder.getProjectRelativePath().toString();
	}

	public String toString() {
		return getElementName();
	}

	@Override
	public ILangElement[] newChildrenArray(int size) {
		return new CompilationUnit[size];
	}

	public int getElementType() {
		return ELangElementTypes.PACKAGE_FRAGMENT;
	}

}
