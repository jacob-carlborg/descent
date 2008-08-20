package descent.tests.assist;

import descent.core.CompletionProposal;

public class KeywordProposal_Test extends AbstractCompletionTest {
	
	public void testAfterModule() throws Exception {
		String s = "module ";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.KEYWORD);
	}
	
	public void testAfterImport() throws Exception {
		String s = "import ";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.KEYWORD);
	}
	
	public void testAlmostEmpty() throws Exception {
		String s = "m";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.KEYWORD,
				"mixin", 0, s.length(),
				"module", 0, s.length()
				);
	}
	
	public void testExtern() throws Exception {
		String s = "extern()";
		
		assertCompletions(null, "test.d", s, 7, CompletionProposal.KEYWORD,
				"C", 7, 7,
				"C++", 7, 7,
				"D", 7, 7,
				"Pascal", 7, 7,
				"System", 7, 7,
				"Windows", 7, 7
				);
	}
	
	public void testExtern2() throws Exception {
		String s = "extern(S)";
		
		assertCompletions(null, "test.d", s, 8, CompletionProposal.KEYWORD,
				"System", 7, 8
				);
	}
	
	public void testBoolVar() throws Exception {
		String s = "bool x = ";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.KEYWORD,
				"false", s.length(), s.length(),
				"true", s.length(), s.length()
				);
	}
	
	public void testFunctionBody() throws Exception {
		String s = "void foo() b { }";
		
		assertCompletions(null, "test.d", s, 12, CompletionProposal.KEYWORD,
				"body", 11, 12
				);
	}
	
	public void testFunctionIn() throws Exception {
		String s = "void foo() i { }";
		
		assertCompletions(null, "test.d", s, 12, CompletionProposal.KEYWORD,
				"in", 11, 12
				);
	}
	
	public void testFunctionOut() throws Exception {
		String s = "void foo() o { }";
		
		assertCompletions(null, "test.d", s, 12, CompletionProposal.KEYWORD,
				"out", 11, 12
				);
	}
	
	public void testFunctionBody2() throws Exception {
		String s = "void foo() b";
		
		assertCompletions(null, "test.d", s, 12, CompletionProposal.KEYWORD,
				"body", 11, 12
				);
	}
	
	public void testFunctionIn2() throws Exception {
		String s = "void foo() i";
		
		assertCompletions(null, "test.d", s, 12, CompletionProposal.KEYWORD,
				"in", 11, 12
				);
	}
	
	public void testFunctionOut2() throws Exception {
		String s = "void foo() o";
		
		assertCompletions(null, "test.d", s, 12, CompletionProposal.KEYWORD,
				"out", 11, 12
				);
	}

}
