package dtool.tests.ref;

import org.eclipse.core.runtime.CoreException;

import mmrnmhrm.tests.SampleMainProject;

public class FindDef__SingleModuleCommon extends FindDef__Common {

	public static final String TEST_SRCFOLDER = SampleMainProject.TEST_SRC_REFS;

	protected void prepTestModule(String testfile) throws CoreException {
		sourceModule = SampleMainProject.getModule(TEST_SRCFOLDER +"/"+ testfile); 
		targetModule = null;
	}

}