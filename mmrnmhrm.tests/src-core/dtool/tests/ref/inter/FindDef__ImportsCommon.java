package dtool.tests.ref.inter;

import java.io.IOException;

import melnorme.miscutil.ExceptionAdapter;
import mmrnmhrm.tests.SampleMainProject;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import dtool.ast.definitions.Module;
import dtool.tests.ref.FindDef__Common;

public abstract class FindDef__ImportsCommon extends FindDef__Common {
	
	public static final String TEST_SRCFOLDER = SampleMainProject.TEST_SRC3;

	private static Module defaultModule;

	protected static void staticTestInit(String testSrcFile) {
		FindDef__Common.staticTestInit(testSrcFile);
		try {
			defaultModule = getTestModule(testSrcFile);
		} catch (CoreException ce) {
			ExceptionAdapter.unchecked(ce);
		}
	}
	
	protected static Module getTestModule(String path) throws CoreException {
		return SampleMainProject.getModule(TEST_SRCFOLDER +"/"+ path);
	}

	public FindDef__ImportsCommon(int offset, int targetOffset, String targetFile)
			throws IOException, CoreException {
		this(null, offset, targetOffset, targetFile);
	}
	
	public FindDef__ImportsCommon(Module newModule, 
			int defOffset, int refOffset, String targetFile)
	throws IOException, CoreException {
		this.offset = defOffset;
		this.targetOffset = refOffset;
		if(newModule == null) {
			sourceModule = defaultModule;
		} else {
			sourceModule = newModule;
		}
		if(targetFile == null) {
			targetModule = null;
		} else {
			targetModule = getTestModule(targetFile);
		}
	}

	@Test
	public void test() throws Exception {
		assertFindReF(sourceModule, offset, targetModule, targetOffset);
	}

}