package descent.tests.assist;

import descent.core.CompletionProposal;

public class CompletionOnModule_Test extends AbstractCompletionTest {
	
	public void testDefaultPackageWithEmptyModuleName() throws Exception {
		assertCompletions(null, "test.d", "module ", 7, 
				CompletionProposal.PACKAGE_REF, "test", 7, 7);
	}
	
	public void testNonDefaultPackageWithEmptyModuleName() throws Exception {
		assertCompletions("foo", "test.d", "module ", 7, 
				CompletionProposal.PACKAGE_REF, "foo.test", 7, 7);
	}
	
	public void testDefaultPackageWithNonEmptyModuleName() throws Exception {
		assertCompletions(null, "test.d", "module te", 9, 
				CompletionProposal.PACKAGE_REF, "test", 7, 9);
	}
	
	public void testNonDefaultPackageWithNonEmptyModuleName() throws Exception {
		assertCompletions("foo", "test.d", "module fo", 9, 
				CompletionProposal.PACKAGE_REF, "foo.test", 7, 9);
	}
	
	public void testNonDefaultPackageWithNonEmptyModuleNameWithDot() throws Exception {
		assertCompletions("foo", "test.d", "module foo.", 11, 
				CompletionProposal.PACKAGE_REF, "foo.test", 7, 11);
	}
	
	public void testNonDefaultPackageWithNonEmptyModuleNameInTheMiddle() throws Exception {
		assertCompletions("foo", "test.d", "module foo.", 9, // fo[cursor]o. 
				CompletionProposal.PACKAGE_REF, "foo.test", 7, 11);
	}

}
