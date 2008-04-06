package descent.tests.assist;

import descent.core.CompletionProposal;

public class CompletionOnScope_Test extends AbstractCompletionTest {
	
	public void testObjectOnModuleScope() throws Exception {
		String s = "O";
		
		assertCompletions(null, "test.d", s, 1, CompletionProposal.TYPE_REF, 
				"Object", 0, 1,
				"OffsetTypeInfo", 0, 1
				);
	}
	
	public void testObjectOnModuleScopeDontSuggestFunction() throws Exception {
		String s = "void Oops() { } O";
		
		assertCompletions(null, "test.d", s, 17, CompletionProposal.TYPE_REF, 
				"Object", 16, 17,
				"OffsetTypeInfo", 16, 17
				);
	}
	
	public void testObjectOnFunctionScope() throws Exception {
		String s = "void foo() { O }";
		
		assertCompletions(null, "test.d", s, 14, CompletionProposal.TYPE_REF, 
				"Object", 13, 14,
				"OffsetTypeInfo", 13, 14);
	}
	
	public void testFunctionOnFunctionScope() throws Exception {
		String s = "void foo() { f }";
		
		assertCompletions(null, "test.d", s, 14, CompletionProposal.METHOD_REF, 
				"foo()", 13, 14
				);
	}
	
	public void testVariableOnFunctionScope() throws Exception {
		String s = "void foo(int xVar) { x }";
		
		assertCompletions(null, "test.d", s, 22, CompletionProposal.LOCAL_VARIABLE_REF, 
				"xVar", 21, 22);
	}
	
	public void testVariableOnIf() throws Exception {
		String s = "void foo(int xVar) { if(x }";
		
		assertCompletions(null, "test.d", s, 25, CompletionProposal.LOCAL_VARIABLE_REF, 
				"xVar", 24, 25);
	}
	
	public void testVariableInsideIf() throws Exception {
		String s = "void foo(int xVar) { if(true) { x }";
		
		assertCompletions(null, "test.d", s, 33,  CompletionProposal.LOCAL_VARIABLE_REF, 
				"xVar", 32, 33);
	}
	
	public void testDontSuggestFurtherInScope() throws Exception {
		String s = "void foo(int xVar) { if(x) { } int xxxVar; }";
		
		assertCompletions(null, "test.d", s, 25, CompletionProposal.LOCAL_VARIABLE_REF, 
				"xVar", 24, 25);
	}
	
	public void testWithScope() throws Exception {
		String s = "void foo(Object o) { with(o) { no } }";
		
		assertCompletions(null, "test.d", s, 33, CompletionProposal.METHOD_REF, 
				"notifyRegister()", 31, 33,
				"notifyUnRegister()", 31, 33
				);
	}

}
