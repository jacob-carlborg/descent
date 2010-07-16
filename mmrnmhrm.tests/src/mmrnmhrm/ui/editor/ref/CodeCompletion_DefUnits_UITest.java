package mmrnmhrm.ui.editor.ref;

import mmrnmhrm.tests.SampleMainProject;

import org.junit.BeforeClass;

import dtool.tests.ref.cc.CodeCompletion_DefUnitsTest;

public class CodeCompletion_DefUnits_UITest extends CodeCompletion_DefUnitsTest {
	
	@BeforeClass
	public static void commonSetUp() throws Exception {
		CodeCompletion_DefUnitsTest.commonSetUp();
		CodeCompletion__UICommon.setupWithFile(
				SampleMainProject.deeProj, CodeCompletion_DefUnitsTest.TEST_SRCFILE);
		ccTester = CodeCompletion__UICommon.ccTester;
	}
}

