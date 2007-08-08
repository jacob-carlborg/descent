package mmrnmhrm.core.model;

import mmrnmhrm.core.model.lang.ILangElement;
import mmrnmhrm.core.model.lang.LangPackageFragment;
import mmrnmhrm.core.model.lang.LangProject;
import mmrnmhrm.core.model.lang.LangSourceFolder;

import org.eclipse.core.resources.IFolder;

public class DeeSourceFolder extends LangSourceFolder implements IDeeSourceRoot {
	
	public static final DeeSourceFolder[] NO_ELEMENTS = new DeeSourceFolder[0];

	public DeeSourceFolder(IFolder folder, LangProject parent) {
		super(parent, folder);
	}
	
	@Override
	public ILangElement[] newChildrenArray(int size) {
		return new LangPackageFragment[size]; // no children for now
	}
	
	public String getSourceRootKindString() {
		return "src";
	}

	@Override
	protected boolean isValidPackageFragmentFolder(IFolder folder) {
		return DeeNameRules.isValidPackageName(folder.getName());
	}

}
