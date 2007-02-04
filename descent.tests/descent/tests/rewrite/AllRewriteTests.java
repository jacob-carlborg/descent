package descent.tests.rewrite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllRewriteTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for descent.tests.rewrite");
		//$JUnit-BEGIN$
		suite.addTestSuite(RewriteAggregateDeclarationTest.class);
		suite.addTestSuite(RewriteAliasDeclarationTest.class);
		suite.addTestSuite(RewriteAliasTemplateParameterTest.class);
		suite.addTestSuite(RewriteAlignDeclarationTest.class);		
		suite.addTestSuite(RewriteArgumentTest.class);
		suite.addTestSuite(RewriteArrayAccessTest.class);
		suite.addTestSuite(RewriteArrayInitializerTest.class);
		suite.addTestSuite(RewriteArrayLiteralTest.class);		
		//$JUnit-END$
		return suite;
	}

}
