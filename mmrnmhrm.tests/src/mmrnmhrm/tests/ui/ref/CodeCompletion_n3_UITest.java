package mmrnmhrm.tests.ui.ref;

import mmrnmhrm.tests.SampleMainProject;

import org.junit.BeforeClass;

import dtool.tests.ref.cc.CodeCompletion_n3Test;

public class CodeCompletion_n3_UITest extends CodeCompletion_n3Test {
	
	@BeforeClass
	public static void commonSetUp() throws Exception {
		CodeCompletion_n3Test.commonSetUp();
		CodeCompletion__UICommon.setupWithFile(
				SampleMainProject.deeProj, CodeCompletion_n3Test.TEST_SRCFILE);
		ccTester = CodeCompletion__UICommon.ccTester;
	}

}

