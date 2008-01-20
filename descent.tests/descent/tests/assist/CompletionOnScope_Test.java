package descent.tests.assist;

import descent.core.CompletionProposal;

public class CompletionOnScope_Test extends AbstractCompletionTest {
	
	public void testObjectOnModuleScope() throws Exception {
		assertCompletions(null, "test.d", "O", 1, 
				CompletionProposal.TYPE_REF, "Object", 0, 1,
				CompletionProposal.TYPE_REF, "OffsetTypeInfo", 0, 1,
				CompletionProposal.KEYWORD, "override", 0, 1);
	}
	
	public void testObjectOnModuleScopeDontSuggestFunction() throws Exception {
		assertCompletions(null, "test.d", "void Oops() { } O", 17, 
				CompletionProposal.TYPE_REF, "Object", 16, 17,
				CompletionProposal.TYPE_REF, "OffsetTypeInfo", 16, 17,
				CompletionProposal.KEYWORD, "override", 16, 17);
	}
	
	public void testObjectOnFunctionScope() throws Exception {
		assertCompletions(null, "test.d", "void foo() { O }", 14, 
				CompletionProposal.TYPE_REF, "Object", 13, 14,
				CompletionProposal.TYPE_REF, "OffsetTypeInfo", 13, 14);
	}
	
	public void testFunctionOnFunctionScope() throws Exception {
		assertCompletions(null, "test.d", "void foo() { f }", 14, 
				CompletionProposal.KEYWORD, "false", 13, 14,
				CompletionProposal.KEYWORD, "float", 13, 14,
				CompletionProposal.METHOD_REF, "foo()", 13, 14,
				CompletionProposal.KEYWORD, "for", 13, 14,
				CompletionProposal.KEYWORD, "foreach", 13, 14,
				CompletionProposal.KEYWORD, "foreach_reverse", 13, 14,
				CompletionProposal.KEYWORD, "function", 13, 14);
	}
	
	public void testVariableOnFunctionScope() throws Exception {
		assertCompletions(null, "test.d", "void foo(int xVar) { x }", 22, 
				CompletionProposal.FIELD_REF, "xVar", 21, 22);
	}
	
	public void testVariableOnIf() throws Exception {
		assertCompletions(null, "test.d", "void foo(int xVar) { if(x }", 25, 
				CompletionProposal.FIELD_REF, "xVar", 24, 25);
	}
	
	public void testVariableInsideIf() throws Exception {
		assertCompletions(null, "test.d", "void foo(int xVar) { if(true) { x }", 33, 
				CompletionProposal.FIELD_REF, "xVar", 32, 33);
	}
	
	public void testDontSuggestFurtherInScope() throws Exception {
		assertCompletions(null, "test.d", "void foo(int xVar) { if(x) { } int xxxVar; }", 25, 
				CompletionProposal.FIELD_REF, "xVar", 24, 25);
	}
	
	public void testWithScope() throws Exception {
		assertCompletions(null, "test.d", "void foo(Object o) { with(o) { pr } }", 33, 
				CompletionProposal.KEYWORD, "pragma", 31, 33,
				CompletionProposal.METHOD_REF, "print()", 31, 33);
	}

}
