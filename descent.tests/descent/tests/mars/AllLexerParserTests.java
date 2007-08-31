package descent.tests.mars;

import descent.tests.scanner.ScannerTests;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllLexerParserTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.asterite.deditor.tests.mars");
		//$JUnit-BEGIN$
		suite.addTestSuite(Comment_Test.class);
		suite.addTestSuite(Bugs_Test.class);
		suite.addTestSuite(VariableDeclaration_Test.class);
		suite.addTestSuite(Link_Test.class);
		suite.addTestSuite(Class_Test.class);
		suite.addTestSuite(Template_Test.class);
		suite.addTestSuite(Pragma_Test.class);
		suite.addTestSuite(Interface_Test.class);
		suite.addTestSuite(Type_Test.class);
		suite.addTestSuite(Enum_Test.class);
		suite.addTestSuite(Union_Test.class);
		suite.addTestSuite(Struct_Test.class);
		suite.addTestSuite(Import_Test.class);
		suite.addTestSuite(Condition_Test.class);
		suite.addTestSuite(UnitTest_Test.class);
		suite.addTestSuite(Lexer_Test.class);
		suite.addTestSuite(Expression_Test.class);
		suite.addTestSuite(Align_Test.class);
		suite.addTestSuite(CompilationUnit_Test.class);
		suite.addTestSuite(MixinAndTemplateMiixn_Test.class);
		suite.addTestSuite(Typedef_Test.class);
		suite.addTestSuite(Problems_Test.class);
		suite.addTestSuite(Recovery_Tests.class);
		suite.addTestSuite(Function_Test.class);
		suite.addTestSuite(Initializer_Test.class);
		suite.addTestSuite(Alias_Test.class);
		suite.addTestSuite(Modifier_Test.class);
		suite.addTestSuite(Statement_Test.class);
		suite.addTestSuite(Invariant_Test.class);
		suite.addTestSuite(ExtendedSourceRange_Test.class);
		suite.addTestSuite(LexerReplacements_Test.class);
		//suite.addTestSuite(Semantic1_Test.class);
		suite.addTestSuite(ASTConvertion_Test.class);
		suite.addTestSuite(ScannerTests.class);
		//$JUnit-END$
		return suite;
	}

}
