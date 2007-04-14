package mmrnmhrm.core.model;

import org.eclipse.core.resources.IFolder;

public class DeeSourceFolder extends LangSourceFolder implements IDeeSourceRoot {
	
	public DeeSourceFolder(IFolder folder, DeeProject parent) {
		this.folder = folder;
		this.parent = parent;
	}
	
	@Override
	public ILangElement[] newChildrenArray(int size) {
		//TODO finish
		return new ILangElement[size];
	}

	public String getSourceRootKindString() {
		return "src";
	}
}
