package descent.tests.assist;

import descent.core.CompletionProposal;

public class FieldProposal_Test extends AbstractCompletionTest  {
	
	public void testVarWithBasicTypeInFunction() throws Exception {
		String s = "int wxyz; void foo() { }";
		
		int pos = s.lastIndexOf('{') + 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				new int[] { SIGNATURE, TYPE_SIGNATURE, DECLARATION_SIGNATURE, LABEL }, 
				"wxyz", pos, pos, "@4testB4wxyz", "i", "@4test", "wxyz    int - test");
	}
	
	public void testVarWithBasicTypeInFunctionFQN() throws Exception {
		createCompilationUnit("one.two", "three.d", "int wxyz;");
		
		String s = "import one.two.three; void foo() { }";
		
		int pos = s.lastIndexOf('{') + 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				new int[] { SIGNATURE, TYPE_SIGNATURE, DECLARATION_SIGNATURE, LABEL }, 
				"wxyz", pos, pos, "@3one3two5threeB4wxyz", "i", "@3one3two5three", "wxyz    int - one.two.three");
	}
	
	public void testVarWithBasicTypeInFunctionSome() throws Exception {
		String s = "int zxyz; void foo() { z }";
		
		int pos = s.lastIndexOf("z ") + 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				new int[] { SIGNATURE, TYPE_SIGNATURE, DECLARATION_SIGNATURE, LABEL }, 
				"zxyz", pos - 1, pos, "@4testB4zxyz", "i", "@4test", "zxyz    int - test");
	}
	
	public void testVarWithClassTypeInFunction() throws Exception {
		String s = "class SomeClass { } SomeClass wxyz; void foo() { }";
		
		int pos = s.lastIndexOf('{') + 1; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				new int[] { SIGNATURE, TYPE_SIGNATURE, DECLARATION_SIGNATURE, LABEL }, 
				"wxyz", pos, pos, "@4testB4wxyz", "@4testC9SomeClass", "@4test", "wxyz    SomeClass - test");
	}
	
	public void testFieldInFunction() throws Exception {
		String s = "class SomeClass { int wxyz; } void foo(SomeClass c) { c. }";
		
		int pos = s.lastIndexOf('.') + 1;
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				new int[] { TYPE_SIGNATURE, DECLARATION_SIGNATURE, LABEL },
				"alignof", pos, pos, "i", "@4testC9SomeClass", "alignof    int - SomeClass",
				"classinfo", pos, pos, "@6objectC9ClassInfo", "@4testC9SomeClass", "classinfo    TypeInfo - SomeClass",
				"init", pos, pos, "@4testC9SomeClass", "@4testC9SomeClass", "init    SomeClass - SomeClass",
				"mangleof", pos, pos, "Aa", "@4testC9SomeClass", "mangleof    char[] - SomeClass",
				"sizeof", pos, pos, "i", "@4testC9SomeClass", "sizeof    int - SomeClass",
				"stringof", pos, pos, "Aa", "@4testC9SomeClass", "stringof    char[] - SomeClass",
				"wxyz", pos, pos, "i", "@4testC9SomeClass", "wxyz    int - SomeClass"
				);
	}
	
	public void testFieldInFunctionSome() throws Exception {
		String s = "class SomeClass { int wxyz; } void foo(SomeClass c) { c.w }";
		
		int pos = s.lastIndexOf('.') + 2;
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				new int[] { SIGNATURE, TYPE_SIGNATURE, DECLARATION_SIGNATURE, LABEL },
				"wxyz", pos - 1, pos, "@4testC9SomeClassB4wxyz", "i", "@4testC9SomeClass", "wxyz    int - SomeClass"
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
				"wxyz", pos, pos, "@4testB4wxyz", "@4test<9SomeClass#'!^i'", "@4test", "wxyz    SomeClass!(int) - test");
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
	
	public void testDontSuggestFieldFromReachablePrivateImport() throws Exception {
		createCompilationUnit("one.d", "int oneVar;");
		createCompilationUnit("two.d", "import one; int twoVar;");
		
		String s = "import two; void foo() {  }";
		
		int pos = s.length() - 2; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				"twoVar", pos, pos);
	}
	
	public void testSuggestFieldFromReachablePublicImport() throws Exception {
		createCompilationUnit("one.d", "int oneVar;");
		createCompilationUnit("two.d", "public import one; int twoVar;");
		
		String s = "import two; void foo() {  }";
		
		int pos = s.length() - 2; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				"oneVar", pos, pos,
				"twoVar", pos, pos
				);
	}
	
	public void testMixin() throws Exception {
		String s = "template Bar() { int wxyz; } class Foo { mixin Bar!(); } void foo(Foo f) { f.w }";
		
		int pos = s.lastIndexOf(".") + 2; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				"wxyz", pos - 1, pos
				);
	}
	
	public void testNamedMixin() throws Exception {
		String s = "template Bar() { int wxyz; } class Foo { mixin Bar!() xyzw; } void foo(Foo f) { f.x }";
		
		int pos = s.lastIndexOf(".") + 2; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.FIELD_REF,
				"xyzw", pos - 1, pos
				);
	}

}
