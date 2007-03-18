package mmrnmhrm.core.model;

import org.eclipse.core.resources.IFolder;

public class DeeSourceFolder implements IBuildPathEntry {
	public IFolder folder;

	public DeeSourceFolder(IFolder folder) {
		this.folder = folder;
	}

	public String getPathString() {
		return folder.getProjectRelativePath().toPortableString();
	}

	public Object getProjectRelativePath() {
		return folder.getProjectRelativePath();
	}

	public String getKindString() {
		return "src";
	}
	
	public int getKind() {
		return TYPE.DEE_SOURCE_FOLDER;
	}

}
