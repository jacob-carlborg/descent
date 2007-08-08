package mmrnmhrm.core.model;

import mmrnmhrm.core.model.lang.ILangElement;
import mmrnmhrm.core.model.lang.ILangSourceRoot;
import mmrnmhrm.core.model.lang.LangPackageFragment;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;

public class DeePackageFragment extends LangPackageFragment implements IDeeElement {

	public DeePackageFragment(ILangSourceRoot parent, IFolder myfolder) throws CoreException {
		super(parent);
		packageFolder = myfolder;
	}
	
	@Override
	public ILangElement[] newChildrenArray(int size) {
		return new CompilationUnit[size];
	}

	@Override
	protected boolean isValidCompilationUnit(IFile file) {
		return DeeNameRules.isValidCompilationUnitName(file.getName());
	}

	@Override
	protected void disposeElementInfo() {
		setChildren(newChildrenArray(0)); // reset children
	}
}
