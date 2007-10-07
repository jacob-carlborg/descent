package mmrnmhrm.tests.ui.ref;

import mmrnmhrm.tests.SampleMainProject;

import org.junit.BeforeClass;

import dtool.tests.ref.cc.CodeCompletion_n2Test;

public class CodeCompletion_n2_UITest extends CodeCompletion_n2Test {
	
	@BeforeClass
	public static void commonSetUp() throws Exception {
		CodeCompletion_n2Test.commonSetUp();
		CodeCompletion__UICommon.setupWithFile(
				SampleMainProject.deeProj, CodeCompletion_n2Test.TEST_SRCFILE);
		ccTester = CodeCompletion__UICommon.ccTester;
	}
	
}

