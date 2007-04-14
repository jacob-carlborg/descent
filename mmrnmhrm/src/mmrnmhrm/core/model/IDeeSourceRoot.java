package mmrnmhrm.core.model;


public interface IDeeSourceRoot extends ILangSourceRoot {
	interface TYPE {
		int DEE_SOURCE_FOLDER = 1;
		int DEE_LIB_FOLDER = 2;
	}

	String getSourceRootKindString();
}
