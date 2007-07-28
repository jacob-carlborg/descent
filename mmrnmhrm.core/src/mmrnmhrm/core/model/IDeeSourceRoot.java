package mmrnmhrm.core.model;

import org.eclipse.core.runtime.CoreException;

import mmrnmhrm.core.model.lang.ILangSourceRoot;
import mmrnmhrm.core.model.lang.LangPackageFragment;


public interface IDeeSourceRoot extends IDeeElement, ILangSourceRoot {
	interface TYPE {
		int DEE_SOURCE_FOLDER = 1;
		int DEE_LIB_FOLDER = 2;
	}

	/** Gets the PackageFragments of this source root. */
	public LangPackageFragment[] getPackageFragments() throws CoreException;
	
	/** Returns a identifying the kind of Source Root XXX refactor? */
	String getSourceRootKindString();

}
