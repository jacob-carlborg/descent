package descent.tests.assist;

import descent.core.CompletionProposal;

public class MethodProposal_Test extends AbstractCompletionTest {
	
	public void testInVarAssignment() throws Exception {
		String s = "int foo() { return 1; } int x = ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF,
				new int[] { SIGNATURE, TYPE_SIGNATURE, DECLARATION_SIGNATURE, LABEL }, 
				"foo()", pos, pos, "@4test[3fooFZi", "FZi", "@4test", "foo()  int - test");
	}
	
	public void testInVarAssignmentSome() throws Exception {
		String s = "int foo() { return 1; } int x = f";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF,
				new int[] { SIGNATURE, TYPE_SIGNATURE, DECLARATION_SIGNATURE, LABEL }, 
				"foo()", pos - 1, pos, "@4test[3fooFZi", "FZi", "@4test", "foo()  int - test");
	}
	
	public void testClassMethod() throws Exception {
		String s = "void foo(Object obj) { obj. }";
		
		int pos = s.lastIndexOf('.') + 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF,
				new int[] { LABEL },
				"factory()", pos, pos, "factory(char[] classname)  Object - Object",
				"notifyRegister()", pos, pos, "notifyRegister(void delegate(Object) dg)  void - Object",
				"notifyUnRegister()", pos, pos, "notifyUnRegister(void delegate(Object) dg)  void - Object",
//				"opCmp()", pos, pos, "opCmp(Object o)  int - Object",
//				"opEquals()", pos, pos, "opEquals(Object o)  int - Object",
				"print()", pos, pos, "print()  void - Object",
				"toHash()", pos, pos, "toHash()  hash_t - Object",
				"toString()", pos, pos, "toString()  char[] - Object"
				);
	}
	
	public void testDontSuggestInModuleScope() throws Exception {
		String s = "void foo() { } ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}
	
	public void testDontSuggestInModuleScopeSome() throws Exception {
		String s = "void foo() { } f";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}
	
	public void testDontSuggestInVarName() throws Exception {
		String s = "void foo() { } int ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}
	
	public void testDontSuggestInVarNameSome() throws Exception {
		String s = "void foo() { } int f";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}
	
	public void testDontSuggestAfterClass() throws Exception {
		String s = "int wxyz; class ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}
	
	public void testDontSuggestAfterClassSome() throws Exception {
		String s = "int wxyz; class w";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}
	
	public void testDontSuggestAfterInterface() throws Exception {
		String s = "int wxyz; interface ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}
	
	public void testDontSuggestAfterInterfaceSome() throws Exception {
		String s = "int wxyz; interface w";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}
	
	public void testDontSuggestAfterStruct() throws Exception {
		String s = "int wxyz; struct ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}
	
	public void testDontSuggestAfterStructSome() throws Exception {
		String s = "int wxyz; struct w";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}
	
	public void testDontSuggestAfterUnion() throws Exception {
		String s = "int wxyz; union ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}
	
	public void testDontSuggestAfterUnionSome() throws Exception {
		String s = "int wxyz; union ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}
	
	public void testDontSuggestAfterTemplate() throws Exception {
		String s = "int wxyz; template ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}
	
	public void testDontSuggestAfterTemplateSome() throws Exception {
		String s = "int wxyz; template w";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}
	
	public void testDontSuggestAfterClassBaseClass() throws Exception {
		String s = "int wxyz; class Foo : ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}
	
	public void testDontSuggestAfterClassBaseClassSome() throws Exception {
		String s = "int wxyz; class Foo : w";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}
	
	public void testDontSuggestAfterClassBaseClassComma() throws Exception {
		String s = "int wxyz; class Foo : Object, ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}
	
	public void testDontSuggestAfterClassBaseClassCommaSome() throws Exception {
		String s = "int wxyz; class Foo : Object, w";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}
	
	public void testDontSuggestAfterInterfaceBaseClass() throws Exception {
		String s = "int wxyz; interface Foo : ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}
	
	public void testDontSuggestAfterInterfaceBaseClassSome() throws Exception {
		String s = "int wxyz; interface Foo : w";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}
	
	public void testDontSuggestAfterInterfaceBaseClassComma() throws Exception {
		String s = "int wxyz; interface Foo : Object, ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}
	
	public void testDontSuggestAfterInterfaceBaseClassCommaSome() throws Exception {
		String s = "int wxyz; interface Foo : Object, w";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.METHOD_REF);
	}

}
