package descent.tests.rewrite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllRewriteTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for descent.tests.rewrite");
		//$JUnit-BEGIN$
		suite.addTestSuite(RewriteAggregateDeclarationTest.class);
		suite.addTestSuite(RewriteAliasDeclarationTest.class);
		//$JUnit-END$
		return suite;
	}

}
