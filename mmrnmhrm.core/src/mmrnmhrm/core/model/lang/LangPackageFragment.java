package mmrnmhrm.core.model.lang;

import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.core.model.IDeeSourceRoot;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;


public abstract class LangPackageFragment extends LangContainerElement {

	protected IFolder packageFolder;

	public LangPackageFragment(ILangElement parent) {
		super(parent);
	}
	
	public IResource getUnderlyingResource() {
		return packageFolder;
	}
	
	public IDeeSourceRoot getParent() {
		return (IDeeSourceRoot) parent;
	}

	public String getElementName() {
		IPath thispath = this.packageFolder.getProjectRelativePath();
		int common = thispath.matchingFirstSegments(getParent().getProjectRelativePath());
		String str = thispath.removeFirstSegments(common).toString(); 
		return str.replace('/', '.');
	}

	public int getElementType() {
		return ELangElementTypes.PACKAGE_FRAGMENT;
	}
	
	/* -------------- Structure  -------------- */
	
	public void createElementInfo() throws CoreException {
		clearChildren();
		for(IResource resource : packageFolder.members()) {
			if(resource.getType() == IResource.FILE) {
				IFile myfile = (IFile) resource;
				if(isValidCompilationUnit(myfile))
					addChild(new CompilationUnit(this, myfile));
			}
		}
	}
	
	
	protected abstract boolean isValidCompilationUnit(IFile file);

	/** Gets the Compilation Units of this package. */
	public CompilationUnit[] getCompilationUnits() throws CoreException {
		getElementInfo();
		return (CompilationUnit[]) getChildren();
	}

}