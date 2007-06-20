package mmrnmhrm.core.model.lang;


import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;

public abstract class LangSourceFolder extends LangElement implements ILangSourceRoot {

	public IFolder srcfolder;

	public LangSourceFolder(LangElement parent, IFolder folder) {
		super(parent);
		this.srcfolder = folder;
	}

	public String getElementName() {
		return this.srcfolder.getProjectRelativePath().toString();
	}

	public String toString() {
		return getElementName();
	}

	public IPath getProjectRelativePath() {
		return srcfolder.getProjectRelativePath();
	}

	public int getElementType() {
		return ELangElementTypes.SOURCEFOLDER;
	}
	
	public String getSourceRootKindString() {
		return "src";
	}

}