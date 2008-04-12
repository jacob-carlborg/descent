package descent.tests.assist;

import descent.core.CompletionProposal;

public class FieldProposal_Test extends AbstractCompletionTest  {
	
	public void testVarWithBasicType() throws Exception {
		String s = "int wxyz; void foo() { }";
		
		int pos = s.lastIndexOf('{') + 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				new int[] { SIGNATURE, TYPE_SIGNATURE, DECLARATION_SIGNATURE, LABEL }, 
				"wxyz", pos, pos, "@4test/4wxyz", "i", "@4test", "wxyz    int - test");
	}
	
	public void testVarWithClassType() throws Exception {
		String s = "class SomeClass { } SomeClass wxyz; void foo() { }";
		
		int pos = s.lastIndexOf('{') + 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				new int[] { SIGNATURE, TYPE_SIGNATURE, DECLARATION_SIGNATURE, LABEL }, 
				"wxyz", pos, pos, "@4test/4wxyz", "@4testC9SomeClass", "@4test", "wxyz    SomeClass - test");
	}
	
	public void testVarInClassType() throws Exception {
		String s = "class SomeClass { int wxyz; } void foo(SomeClass c) { c. }";
		
		int pos = s.lastIndexOf('.') + 1;
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				new int[] { SIGNATURE, TYPE_SIGNATURE, DECLARATION_SIGNATURE, LABEL },
				"alignof", pos, pos, "@4testC9SomeClass/7alignof", "i", "@4testC9SomeClass", "alignof    int - SomeClass",
				"init", pos, pos, "@4testC9SomeClass/4init", "@4testC9SomeClass", "@4testC9SomeClass", "init    SomeClass - SomeClass",
				"mangleof", pos, pos, "@4testC9SomeClass/8mangleof", "Aa", "@4testC9SomeClass", "mangleof    char[] - SomeClass",
				"sizeof", pos, pos, "@4testC9SomeClass/6sizeof", "i", "@4testC9SomeClass", "sizeof    int - SomeClass",
				"stringof", pos, pos, "@4testC9SomeClass/8stringof", "Aa", "@4testC9SomeClass", "stringof    char[] - SomeClass",
				"wxyz", pos, pos, "@4testC9SomeClass/4wxyz", "i", "@4testC9SomeClass", "wxyz    int - SomeClass"
				);
	}

}
