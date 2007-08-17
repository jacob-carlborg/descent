package mmrnmhrm.core.model;

import mmrnmhrm.core.model.lang.ELangElementTypes;
import mmrnmhrm.core.model.lang.LangProject;

import org.eclipse.core.resources.IFolder;

public class DeeSourceLib extends DeeSourceFolder implements IDeeSourceRoot {

	
	public DeeSourceLib(IFolder folder, LangProject parent) {
		super(folder, parent);
	}

	public int getElementType() {
		return ELangElementTypes.SOURCELIB;
	}

	public String getSourceRootKindString() {
		return "lib";
	}

}
