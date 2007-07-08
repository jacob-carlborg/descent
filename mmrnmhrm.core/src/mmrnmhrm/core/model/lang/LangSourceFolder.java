package mmrnmhrm.core.model.lang;


import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;

public abstract class LangSourceFolder extends LangContainerElement implements ILangSourceRoot {

	public IFolder folder;

	public LangSourceFolder(LangContainerElement parent, IFolder folder) {
		super(parent);
		this.folder = folder;
	}

	public String getElementName() {
		return this.folder.getProjectRelativePath().toString();
	}

	public String toString() {
		return getElementName();
	}

	public IPath getProjectRelativePath() {
		return folder.getProjectRelativePath();
	}

	public int getElementType() {
		return ELangElementTypes.SOURCEFOLDER;
	}
	
	public String getSourceRootKindString() {
		return "src";
	}

}