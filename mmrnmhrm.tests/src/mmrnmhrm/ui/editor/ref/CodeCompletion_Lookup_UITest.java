package mmrnmhrm.ui.editor.ref;

import mmrnmhrm.tests.SampleMainProject;

import org.junit.BeforeClass;

import dtool.tests.ref.cc.CodeCompletion_LookupTest;

public class CodeCompletion_Lookup_UITest extends CodeCompletion_LookupTest {
	
	@BeforeClass
	public static void commonSetUp() throws Exception {
		CodeCompletion_LookupTest.commonSetUp();
		CodeCompletion__UICommon.setupWithFile(
				SampleMainProject.deeProj, CodeCompletion_LookupTest.TEST_SRCFILE);
		ccTester = CodeCompletion__UICommon.ccTester;
	}

}


