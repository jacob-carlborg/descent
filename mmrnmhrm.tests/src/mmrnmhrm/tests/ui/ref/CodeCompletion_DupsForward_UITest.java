package mmrnmhrm.tests.ui.ref;

import mmrnmhrm.tests.SampleMainProject;

import org.junit.BeforeClass;

import dtool.tests.ref.cc.CodeCompletion_DuplicatesTest;

public class CodeCompletion_DupsForward_UITest extends CodeCompletion_DuplicatesTest {
	
	@BeforeClass
	public static void commonSetUp() throws Exception {
		CodeCompletion_DuplicatesTest.commonSetUp();
		CodeCompletion__UICommon.setupWithFile(
				SampleMainProject.deeProj, CodeCompletion_DuplicatesTest.TEST_SRCFILE);
		ccTester = CodeCompletion__UICommon.ccTester;
	}
}

