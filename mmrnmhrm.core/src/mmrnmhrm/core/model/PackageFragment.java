package mmrnmhrm.core.model;

import mmrnmhrm.core.model.lang.ELangElementTypes;
import mmrnmhrm.core.model.lang.ILangElement;
import mmrnmhrm.core.model.lang.LangContainerElement;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public class PackageFragment extends LangContainerElement implements IDeeElement {

	IFolder packageFolder;
	
	public PackageFragment(DeeSourceFolder parent, IFolder myfolder) {
		super(parent);
		packageFolder = myfolder;
	}

	public IResource getUnderlyingResource() {
		return packageFolder;
	}
	
	public IDeeSourceRoot getParent() {
		return (IDeeSourceRoot) parent;
	}
	
	public String getElementName() {
		IPath packpath = packageFolder.getProjectRelativePath();
		IPath parentpath = getParent().getProjectRelativePath();
		IPath newpath = packpath.removeFirstSegments(packpath.matchingFirstSegments(parentpath));
		return newpath.toString().replace('/', '.');
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

	public void updateElement() throws CoreException {
		for(IResource resource : packageFolder.members()) {
			if(resource.getType() == IResource.FILE) {
				IFile myfolder = (IFile) resource;
				addChild(new CompilationUnit(this, myfolder));
			}
		}
	}

	public void updateElementRecursive() throws CoreException {
		updateElement();
		for(CompilationUnit element : getCompilationUnits()) {
			element.updateElementRecursive();
		}
	}

	public CompilationUnit[] getCompilationUnits() {
		return (CompilationUnit[]) getChildren();
	}

}
