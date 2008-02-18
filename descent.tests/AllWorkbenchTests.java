import junit.framework.Test;
import junit.framework.TestSuite;
import descent.tests.assist.CompletionOnBreakStatement_Test;
import descent.tests.assist.CompletionOnCaseStatement_Test;
import descent.tests.assist.CompletionOnContinueStatement_Test;
import descent.tests.assist.CompletionOnDotIdExp_Test;
import descent.tests.assist.CompletionOnGotoStatement_Test;
import descent.tests.assist.CompletionOnImport_Test;
import descent.tests.assist.CompletionOnKeyword_Test;
import descent.tests.assist.CompletionOnModule_Test;
import descent.tests.assist.CompletionOnScope_Test;
import descent.tests.assist.CompletionOnTypeDotIdExp_Test;
import descent.tests.binding.BindingEnum_Test;
import descent.tests.binding.BindingExpression_Test;
import descent.tests.binding.BindingFunction_Test;
import descent.tests.binding.BindingImport_Test;
import descent.tests.binding.BindingLocalSymbol_Test;
import descent.tests.binding.BindingLocalVar_Test;
import descent.tests.binding.BindingTemplate_Test;
import descent.tests.binding.BindingType_Test;
import descent.tests.binding.BindingVar_Test;
import descent.tests.evaluate.Evaluate_Test;
import descent.tests.lookup.LookupPhobos_Test;
import descent.tests.lookup.LookupTemplate_Test;
import descent.tests.lookup.Lookup_Test;
import descent.tests.mangling.CustomSignature_Test;
import descent.tests.model.CreationTest;
import descent.tests.model.HierarchyTest;
import descent.tests.select.CodeSelecType_Test;
import descent.tests.select.CodeSelectEnum_Test;
import descent.tests.select.CodeSelectFunction_Test;
import descent.tests.select.CodeSelectLocalSymbol_Test;
import descent.tests.select.CodeSelectLocalVariable_Test;
import descent.tests.select.CodeSelectModule_Test;
import descent.tests.select.CodeSelectTemplate_Test;
import descent.tests.select.CodeSelectVariable_Test;

/*
 * Here are listed all tests that require a workbench and run relatively
 * slow. You can control which tests to run by modifying the final static
 * fields of this class.
 * 
 * Right click on this file and select: Run as... -> JUnit Plug-in test
 */
public class AllWorkbenchTests {
	
	private final static boolean BINDING = true;
	private final static boolean CODE_COMPLETE = true;
	private final static boolean CODE_EVALUATE = true;
	private final static boolean CODE_SELECT = true;
	private final static boolean LOOKUP = true;
	private final static boolean MODEL = true;
	private final static boolean SIGNATURE = true;
	
	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test which require a workbench");
		
		if (BINDING) {
			suite.addTestSuite(BindingEnum_Test.class);
			suite.addTestSuite(BindingExpression_Test.class);
			suite.addTestSuite(BindingFunction_Test.class);
			suite.addTestSuite(BindingImport_Test.class);
			suite.addTestSuite(BindingLocalSymbol_Test.class);
			suite.addTestSuite(BindingLocalVar_Test.class);
			suite.addTestSuite(BindingTemplate_Test.class);
			suite.addTestSuite(BindingType_Test.class);
			suite.addTestSuite(BindingVar_Test.class);
		}
		
		if (CODE_COMPLETE) {
			suite.addTestSuite(CompletionOnBreakStatement_Test.class);
			suite.addTestSuite(CompletionOnCaseStatement_Test.class);
			suite.addTestSuite(CompletionOnContinueStatement_Test.class);
			suite.addTestSuite(CompletionOnDotIdExp_Test.class);
			suite.addTestSuite(CompletionOnGotoStatement_Test.class);
			suite.addTestSuite(CompletionOnImport_Test.class);
			suite.addTestSuite(CompletionOnKeyword_Test.class);
			suite.addTestSuite(CompletionOnModule_Test.class);
			suite.addTestSuite(CompletionOnScope_Test.class);
			suite.addTestSuite(CompletionOnTypeDotIdExp_Test.class);
		}
		
		if (CODE_EVALUATE) {
			suite.addTestSuite(Evaluate_Test.class);
		}
		
		if (CODE_SELECT) {
			suite.addTestSuite(CodeSelectEnum_Test.class);
			suite.addTestSuite(CodeSelectFunction_Test.class);
			suite.addTestSuite(CodeSelectLocalSymbol_Test.class);
			suite.addTestSuite(CodeSelectLocalVariable_Test.class);
			suite.addTestSuite(CodeSelectModule_Test.class);
			suite.addTestSuite(CodeSelectTemplate_Test.class);
			suite.addTestSuite(CodeSelectVariable_Test.class);
			suite.addTestSuite(CodeSelecType_Test.class);
		}
		
		if (LOOKUP) {
			suite.addTestSuite(Lookup_Test.class);
			suite.addTestSuite(LookupPhobos_Test.class);
			suite.addTestSuite(LookupTemplate_Test.class);
		}
		
		if (MODEL) {
			suite.addTestSuite(CreationTest.class);
			suite.addTestSuite(HierarchyTest.class);
		}
		
		if (SIGNATURE) {
			suite.addTestSuite(CustomSignature_Test.class);
		}
		
		return suite;
	}

}
