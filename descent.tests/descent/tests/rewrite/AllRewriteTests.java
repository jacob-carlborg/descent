package descent.tests.rewrite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllRewriteTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for descent.tests.rewrite");
		//$JUnit-BEGIN$
		suite.addTestSuite(RewriteAggregateDeclarationTest.class);
		suite.addTestSuite(RewriteAliasDeclarationTest.class);
		suite.addTestSuite(RewriteTemplateParameterTest.class);
		suite.addTestSuite(RewriteAlignDeclarationTest.class);		
		suite.addTestSuite(RewriteArgumentTest.class);
		suite.addTestSuite(RewriteInitializerTest.class);
		suite.addTestSuite(RewriteStatementTest.class);
		suite.addTestSuite(RewriteExpressionTest.class);
		suite.addTestSuite(RewriteTypeTest.class);
		suite.addTestSuite(RewriteBaseClassTest.class);
		suite.addTestSuite(RewriteDDocCommentTest.class);
		suite.addTestSuite(RewriteDebugAssignmentTest.class);		
		suite.addTestSuite(RewriteDebugDeclarationTest.class);
		suite.addTestSuite(RewriteEnumDeclarationTest.class);
		suite.addTestSuite(RewriteExternDeclarationTest.class);
		suite.addTestSuite(RewriteModuleDeclarationTest.class);
		suite.addTestSuite(RewriteStaticIfDeclarationTest.class);
		suite.addTestSuite(RewriteMixinDeclarationTest.class);
		suite.addTestSuite(RewriteImportDeclarationTest.class);
		suite.addTestSuite(RewriteFunctionDeclarationTest.class);
		//$JUnit-END$
		return suite;
	}

}
