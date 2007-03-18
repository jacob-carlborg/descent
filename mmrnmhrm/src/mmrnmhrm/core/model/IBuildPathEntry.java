package mmrnmhrm.core.model;

public interface IBuildPathEntry {
	interface TYPE {
		int DEE_SOURCE_FOLDER = 1;
		int DEE_LIB_FOLDER = 2;
	}

	String getPathString();

	Object getProjectRelativePath();

	String getKindString();
}
