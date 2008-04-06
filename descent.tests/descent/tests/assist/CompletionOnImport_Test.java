package descent.tests.assist;

import descent.core.CompletionProposal;

public class CompletionOnImport_Test extends AbstractCompletionTest {
	
	// For now we'll ignore the standard library
	
	public void testNoImports() throws Exception {
		assertCompletions(null, "test.d", "import ", 7, CompletionProposal.COMPILATION_UNIT_REF);
	}
	
	public void testNothingIfEmpty() throws Exception {
		createCompilationUnit("foo", "file.d", "");
		assertCompletions(null, "test.d", "import ", 7, CompletionProposal.COMPILATION_UNIT_REF);
	}
	
	public void testOther() throws Exception {
		createCompilationUnit("foo", "file.d", "");
		assertCompletions(null, "test.d", "import f", 8, CompletionProposal.COMPILATION_UNIT_REF, 
				"foo.file", 7, 8);
	}
	
	public void testSkipMe() throws Exception {
		createCompilationUnit("foo", "file.d", "");
		assertCompletions(null, "test.d", "import t", 8, CompletionProposal.COMPILATION_UNIT_REF);
	}
	
	public void testOnDotPlusSkipMeNested() throws Exception {
		createCompilationUnit("foo", "file.d", "");
		assertCompletions("foo", "test.d", "import foo.", 11, CompletionProposal.COMPILATION_UNIT_REF, 
				"foo.file", 7, 11);
	}
	
	public void testShowMany() throws Exception {
		createCompilationUnit("foo", "file.d", "");
		createCompilationUnit("foo.bar", "other.d", "");
		assertCompletions("foo", "test.d", "import f", 8, CompletionProposal.COMPILATION_UNIT_REF, 
				"foo.bar.other", 7, 8,
				"foo.file", 7, 8
				);
	}
	
	public void testShowManyIgnoreCase() throws Exception {
		createCompilationUnit("foo", "file.d", "");
		createCompilationUnit("foo.bar", "other.d", "");
		assertCompletions("foo", "test.d", "import fO", 9, CompletionProposal.COMPILATION_UNIT_REF, 
				"foo.bar.other", 7, 9,
				"foo.file", 7, 9
				);
	}
	
}
