package descent.tests.assist;

import descent.core.CompletionProposal;

public class CompletionOnImport_Test extends AbstractCompletionTest {
	
	// For now we'll ignore the standard library
	
	public void testNoImports() throws Exception {
		assertCompletions(null, "test.d", "import ", 7);
	}
	
	public void testNothingIfEmpty() throws Exception {
		createCompilationUnit("foo", "file.d", "");
		assertCompletions(null, "test.d", "import ", 7);
	}
	
	public void testOther() throws Exception {
		createCompilationUnit("foo", "file.d", "");
		assertCompletions(null, "test.d", "import f", 8,
			CompletionProposal.PACKAGE_REF, "foo.file", 7, 8);
	}
	
	public void testSkipMe() throws Exception {
		createCompilationUnit("foo", "file.d", "");
		assertCompletions(null, "test.d", "import t", 8);
	}
	
	public void testOnDotPlusSkipMeNested() throws Exception {
		createCompilationUnit("foo", "file.d", "");
		assertCompletions("foo", "test.d", "import foo.", 11,
				CompletionProposal.PACKAGE_REF, "foo.file", 7, 11);
	}
	
	public void testShowMany() throws Exception {
		createCompilationUnit("foo", "file.d", "");
		createCompilationUnit("foo.bar", "other.d", "");
		assertCompletions("foo", "test.d", "import f", 8,
				CompletionProposal.PACKAGE_REF, "foo.bar.other", 7, 8,
				CompletionProposal.PACKAGE_REF, "foo.file", 7, 8
				);
	}
	
	public void testShowManyIgnoreCase() throws Exception {
		createCompilationUnit("foo", "file.d", "");
		createCompilationUnit("foo.bar", "other.d", "");
		assertCompletions("foo", "test.d", "import fO", 9,
				CompletionProposal.PACKAGE_REF, "foo.bar.other", 7, 9,
				CompletionProposal.PACKAGE_REF, "foo.file", 7, 9
				);
	}
	
}
