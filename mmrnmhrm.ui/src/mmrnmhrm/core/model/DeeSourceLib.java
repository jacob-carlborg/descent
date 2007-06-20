package mmrnmhrm.core.model;

import mmrnmhrm.core.model.lang.ELangElementTypes;
import mmrnmhrm.core.model.lang.ILangElement;
import mmrnmhrm.core.model.lang.LangElement;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public class DeeSourceLib extends LangElement implements IDeeSourceRoot {

	public IFolder folder;
	
	public DeeSourceLib(DeeProject parent, IFolder folder) {
		super(parent);
		this.folder = folder;
	}

	@Override
	public ILangElement[] newChildrenArray(int size) {
		return null; // not implemented
	}

	public String getElementName() {
		return this.folder.getProjectRelativePath().toString();
	}

	public int getElementType() {
		return ELangElementTypes.SOURCELIB;
	}

	public String getSourceRootKindString() {
		return "lib";
	}

	public IPath getProjectRelativePath() {
		return folder.getProjectRelativePath();
	}
	
	public String toString() {
		return getElementName();
	}

	public void refreshElementChildren() throws CoreException {
		// TODO Auto-generated method stub
	}

}
