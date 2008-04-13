package descent.tests.assist;

import descent.core.CompletionProposal;

public class FieldProposal_Test extends AbstractCompletionTest  {
	
	public void testVarWithBasicTypeInFunction() throws Exception {
		String s = "int wxyz; void foo() { }";
		
		int pos = s.lastIndexOf('{') + 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				new int[] { SIGNATURE, TYPE_SIGNATURE, DECLARATION_SIGNATURE, LABEL }, 
				"wxyz", pos, pos, "@4test/4wxyz", "i", "@4test", "wxyz    int - test");
	}
	
	public void testVarWithBasicTypeInFunctionSome() throws Exception {
		String s = "int wxyz; void foo() { w }";
		
		int pos = s.lastIndexOf("w ") + 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				new int[] { SIGNATURE, TYPE_SIGNATURE, DECLARATION_SIGNATURE, LABEL }, 
				"wxyz", pos - 1, pos, "@4test/4wxyz", "i", "@4test", "wxyz    int - test");
	}
	
	public void testVarWithClassTypeInFunction() throws Exception {
		String s = "class SomeClass { } SomeClass wxyz; void foo() { }";
		
		int pos = s.lastIndexOf('{') + 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				new int[] { SIGNATURE, TYPE_SIGNATURE, DECLARATION_SIGNATURE, LABEL }, 
				"wxyz", pos, pos, "@4test/4wxyz", "@4testC9SomeClass", "@4test", "wxyz    SomeClass - test");
	}
	
	public void testFieldInFunction() throws Exception {
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
	
	public void testFieldInFunctionSome() throws Exception {
		String s = "class SomeClass { int wxyz; } void foo(SomeClass c) { c.w }";
		
		int pos = s.lastIndexOf('.') + 2;
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				new int[] { SIGNATURE, TYPE_SIGNATURE, DECLARATION_SIGNATURE, LABEL },
				"wxyz", pos - 1, pos, "@4testC9SomeClass/4wxyz", "i", "@4testC9SomeClass", "wxyz    int - SomeClass"
				);
	}
	
	public void testShortLabelForPointer() throws Exception {
		String s = 
			"class SomeClass { } " + 
			"SomeClass* wxyz; void foo() { }";
		
		int pos = s.lastIndexOf('{') + 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				new int[] { LABEL }, 
				"wxyz", pos, pos, "wxyz    SomeClass* - test");
	}
	
	public void testShortLabelForStaticArray() throws Exception {
		String s = 
			"class SomeClass { } " + 
			"SomeClass[] wxyz; void foo() { }";
		
		int pos = s.lastIndexOf('{') + 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				new int[] { LABEL }, 
				"wxyz", pos, pos, "wxyz    SomeClass[] - test");
	}
	
	public void testShortLabelForAssociativeArray() throws Exception {
		String s = 
			"class SomeClass { } " + 
			"SomeClass[Object] wxyz; void foo() { }";
		
		int pos = s.lastIndexOf('{') + 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				new int[] { LABEL }, 
				"wxyz", pos, pos, "wxyz    SomeClass[Object] - test");
	}
	
	public void testVarWithTemplateInstanceTypeInFunction() throws Exception {
		String s = "class SomeClass(T) { } SomeClass!(int) wxyz; void foo() { }";
		
		int pos = s.lastIndexOf('{') + 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				new int[] { SIGNATURE, TYPE_SIGNATURE, DECLARATION_SIGNATURE, LABEL }, 
				"wxyz", pos, pos, "@4test/4wxyz", "@4test<9SomeClass#'!^i'", "@4test", "wxyz    SomeClass!(int) - test");
	}
	
	public void testVarWithBasicTypeInModuleScope() throws Exception {
		String s = "int wxyz; int wxya = ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				"wxya", pos, pos,
				"wxyz", pos, pos);
	}
	
	public void testVarWithBasicTypeInModuleScopeSome() throws Exception {
		String s = "int wxyz; int wxya = w";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				"wxya", pos - 1, pos,
				"wxyz", pos - 1, pos);
	}	
	
	public void testVarWithBasicTypeInFunctionScope() throws Exception {
		String s = "int wxyz; void foo() { int wxya =  }";
		
		int pos = s.lastIndexOf('=') + 2; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				"wxyz", pos, pos);
	}
	
	public void testVarWithBasicTypeInFunctionScopeSome() throws Exception {
		String s = "int wxyz; void foo() { int wxya = w }";
		
		int pos = s.lastIndexOf('=') + 3; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				"wxyz", pos - 1, pos);
	}
	
	public void testDontSuggestVarsInVarName() throws Exception {
		String s = "int wxyz; int ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF);
	}
	
	public void testDontSuggestVarsInVarNameInFunction() throws Exception {
		String s = "int wxyz; void foo() { int  }";
		
		int pos = s.lastIndexOf('}') - 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF);
	}
	
	public void testDontSuggestAfterClass() throws Exception {
		String s = "int wxyz; class ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF);
	}
	
	public void testDontSuggestAfterClassSome() throws Exception {
		String s = "int wxyz; class w";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF);
	}
	
	public void testDontSuggestAfterInterface() throws Exception {
		String s = "int wxyz; interface ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF);
	}
	
	public void testDontSuggestAfterInterfaceSome() throws Exception {
		String s = "int wxyz; interface w";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF);
	}
	
	public void testDontSuggestAfterStruct() throws Exception {
		String s = "int wxyz; struct ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF);
	}
	
	public void testDontSuggestAfterStructSome() throws Exception {
		String s = "int wxyz; struct w";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF);
	}
	
	public void testDontSuggestAfterUnion() throws Exception {
		String s = "int wxyz; union ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF);
	}
	
	public void testDontSuggestAfterUnionSome() throws Exception {
		String s = "int wxyz; union ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF);
	}
	
	public void testDontSuggestAfterTemplate() throws Exception {
		String s = "int wxyz; template ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF);
	}
	
	public void testDontSuggestAfterTemplateSome() throws Exception {
		String s = "int wxyz; template w";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF);
	}
	
	public void testDontSuggestAfterClassBaseClass() throws Exception {
		String s = "int wxyz; class Foo : ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF);
	}
	
	public void testDontSuggestAfterClassBaseClassSome() throws Exception {
		String s = "int wxyz; class Foo : w";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF);
	}
	
	public void testDontSuggestAfterClassBaseClassComma() throws Exception {
		String s = "int wxyz; class Foo : Object, ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF);
	}
	
	public void testDontSuggestAfterClassBaseClassCommaSome() throws Exception {
		String s = "int wxyz; class Foo : Object, w";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF);
	}
	
	public void testDontSuggestAfterInterfaceBaseClass() throws Exception {
		String s = "int wxyz; interface Foo : ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF);
	}
	
	public void testDontSuggestAfterInterfaceBaseClassSome() throws Exception {
		String s = "int wxyz; interface Foo : w";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF);
	}
	
	public void testDontSuggestAfterInterfaceBaseClassComma() throws Exception {
		String s = "int wxyz; interface Foo : Object, ";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF);
	}
	
	public void testDontSuggestAfterInterfaceBaseClassCommaSome() throws Exception {
		String s = "int wxyz; interface Foo : Object, w";
		
		int pos = s.length(); 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF);
	}

}
