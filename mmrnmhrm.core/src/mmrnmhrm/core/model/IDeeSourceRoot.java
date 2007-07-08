package mmrnmhrm.core.model;

import mmrnmhrm.core.model.lang.ILangSourceRoot;


public interface IDeeSourceRoot extends IDeeElement, ILangSourceRoot {
	interface TYPE {
		int DEE_SOURCE_FOLDER = 1;
		int DEE_LIB_FOLDER = 2;
	}

	public PackageFragment[] getPackageFragments();
	
	/** Returns a identifying the kind of Source Root XXX refactor? */
	String getSourceRootKindString();

}
