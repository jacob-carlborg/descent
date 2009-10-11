package descent.tests.assist;

import descent.core.CompletionProposal;

public class LocalVariableProposal_Test extends AbstractCompletionTest {
	
	public void testInEmpty() throws Exception {
		String s = "void foo(int wxyz) {  }";
		
		int pos = s.lastIndexOf('{') + 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF,
				new int[] { TYPE_SIGNATURE, LABEL }, 
				"wxyz", pos, pos, "i", "wxyz : int");
	}
	
	public void testInEmptySome() throws Exception {
		String s = "void foo(int wxyz) { w }";
		
		int pos = s.lastIndexOf('{') + 3; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF,
				new int[] { TYPE_SIGNATURE, LABEL }, 
				"wxyz", pos - 1, pos, "i", "wxyz : int");
	}
	
	public void testInAssignment() throws Exception {
		String s = "int someVar; void foo(int wxyz) { someVar =  }";
		
		int pos = s.lastIndexOf('=') + 2; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF,
				new int[] { TYPE_SIGNATURE, LABEL }, 
				"wxyz", pos, pos, "i", "wxyz : int");
	}
	
	public void testInAssignmentSome() throws Exception {
		String s = "int someVar; void foo(int wxyz) { someVar = w }";
		
		int pos = s.lastIndexOf('=') + 3; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF,
				new int[] { TYPE_SIGNATURE, LABEL }, 
				"wxyz", pos - 1, pos, "i", "wxyz : int");
	}
	
	public void testInIf() throws Exception {
		String s = "int someVar; void foo(int wxyz) { if () { } }";
		
		int pos = s.lastIndexOf("if") + 4; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF,
				new int[] { TYPE_SIGNATURE, LABEL }, 
				"wxyz", pos, pos, "i", "wxyz : int");
	}
	
	public void testInIfSome() throws Exception {
		String s = "int someVar; void foo(int wxyz) { if (w) { } }";
		
		int pos = s.lastIndexOf("if") + 5; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF,
				new int[] { TYPE_SIGNATURE, LABEL }, 
				"wxyz", pos - 1, pos, "i", "wxyz : int");
	}
	
	public void testDontSuggestAfterType() throws Exception {
		String s = "int someVar; void foo(int wxyz) { int  }";
		
		int pos = s.lastIndexOf("}") - 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF);
	}
	
	public void testDontSuggestAfterTypeSome() throws Exception {
		String s = "int someVar; void foo(int wxyz) { int w }";
		
		int pos = s.lastIndexOf("}") - 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF);
	}
	
	public void testDontSuggestAfterClass() throws Exception {
		String s = "int someVar; void foo(int wxyz) { class  }";
		
		int pos = s.lastIndexOf("}") - 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF);
	}
	
	public void testDontSuggestAfterClassSome() throws Exception {
		String s = "int someVar; void foo(int wxyz) { class w }";
		
		int pos = s.lastIndexOf("}") - 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF);
	}
	
	public void testDontSuggestAfterInterface() throws Exception {
		String s = "int someVar; void foo(int wxyz) { interface  }";
		
		int pos = s.lastIndexOf("}") - 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF);
	}
	
	public void testDontSuggestAfterInterfaceSome() throws Exception {
		String s = "int someVar; void foo(int wxyz) { interface w }";
		
		int pos = s.lastIndexOf("}") - 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF);
	}
	
	public void testDontSuggestAfterStruct() throws Exception {
		String s = "int someVar; void foo(int wxyz) { struct  }";
		
		int pos = s.lastIndexOf("}") - 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF);
	}
	
	public void testDontSuggestAfterStructSome() throws Exception {
		String s = "int someVar; void foo(int wxyz) { struct w }";
		
		int pos = s.lastIndexOf("}") - 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF);
	}
	
	public void testDontSuggestAfterUnion() throws Exception {
		String s = "int someVar; void foo(int wxyz) { union  }";
		
		int pos = s.lastIndexOf("}") - 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF);
	}
	
	public void testDontSuggestAfterUnionSome() throws Exception {
		String s = "int someVar; void foo(int wxyz) { union w }";
		
		int pos = s.lastIndexOf("}") - 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF);
	}
	
	public void testDontSuggestAfterClassBaseClass() throws Exception {
		String s = "int someVar; void foo(int wxyz) { class Foo :  }";
		
		int pos = s.lastIndexOf("}") - 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF);
	}
	
	public void testDontSuggestAfterClassBaseClassSome() throws Exception {
		String s = "int someVar; void foo(int wxyz) { class Foo : w }";
		
		int pos = s.lastIndexOf("}") - 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF);
	}
	
	public void testDontSuggestAfterClassBaseClassComma() throws Exception {
		String s = "int someVar; void foo(int wxyz) { class Foo : Object,  }";
		
		int pos = s.lastIndexOf("}") - 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF);
	}
	
	public void testDontSuggestAfterClassBaseClassCommaSome() throws Exception {
		String s = "int someVar; void foo(int wxyz) { class Foo : Object, w }";
		
		int pos = s.lastIndexOf("}") - 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF);
	}
	
	public void testDontSuggestAfterInterfaceBaseClass() throws Exception {
		String s = "int someVar; void foo(int wxyz) { interface Foo :  }";
		
		int pos = s.lastIndexOf("}") - 1;
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF);
	}
	
	public void testDontSuggestAfterInterfaceBaseClassSome() throws Exception {
		String s = "int someVar; void foo(int wxyz) { interface Foo : w }";
		
		int pos = s.lastIndexOf("}") - 1;
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF);
	}
	
	public void testDontSuggestAfterInterfaceBaseClassComma() throws Exception {
		String s = "int someVar; void foo(int wxyz) { interface Foo : Object,  }";
		
		int pos = s.lastIndexOf("}") - 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF);
	}
	
	public void testDontSuggestAfterInterfaceBaseClassCommaSome() throws Exception {
		String s = "int someVar; void foo(int wxyz) { interface Foo : Object, w }";
		
		int pos = s.lastIndexOf("}") - 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.LOCAL_VARIABLE_REF);
	}

}
