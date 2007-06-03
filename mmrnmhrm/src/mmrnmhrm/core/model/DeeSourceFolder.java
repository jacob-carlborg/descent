package mmrnmhrm.core.model;

import mmrnmhrm.core.model.lang.ILangElement;
import mmrnmhrm.core.model.lang.LangSourceFolder;

import org.eclipse.core.resources.IFolder;

public class DeeSourceFolder extends LangSourceFolder implements IDeeSourceRoot {
	
	public DeeSourceFolder(IFolder folder, DeeProject parent) {
		super(parent, folder);
	}
	
	@Override
	public ILangElement[] newChildrenArray(int size) {
		return null; // no children for now
	}

	public String getSourceRootKindString() {
		return "src";
	}
}
