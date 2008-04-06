package descent.tests.assist;

import descent.core.CompletionProposal;

public class CompletionOnDotIdExp_Test extends AbstractCompletionTest {
	
	public void testOnEnum() throws Exception {
		String s = "enum X { a, b, c } void foo() { int x = X.";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.FIELD_REF, 
				"alignof", s.length(), s.length(),
				"init", s.length(), s.length(),
				"mangleof", s.length(), s.length(),
				"max", s.length(), s.length(),
				"min", s.length(), s.length(),
				"sizeof", s.length(), s.length(),
				"stringof", s.length(), s.length()
				);
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.ENUM_MEMBER, 
				"a", s.length(), s.length(),
				"b", s.length(), s.length(),
				"c", s.length(), s.length()
				);
	}
	
	public void testOnEnumWithSome() throws Exception {
		String s = "enum X { a, b, c } void foo() { int x = X.a";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.FIELD_REF, 
				"alignof", s.length() - 1, s.length()
				);
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.ENUM_MEMBER, 
				"a", s.length() - 1, s.length()
				);
	}
	
	public void testOnStaticArrayType() throws Exception {
		String s = "void foo() { int[3] x; auto y = x.";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.FIELD_REF, 
				"alignof", s.length(), s.length(),
				"dup", s.length(), s.length(),
				"init", s.length(), s.length(),
				"length", s.length(), s.length(),
				"mangleof", s.length(), s.length(),
				"ptr", s.length(), s.length(),
				"reverse", s.length(), s.length(),
				"sizeof", s.length(), s.length(),
				"sort", s.length(), s.length(),
				"stringof", s.length(), s.length()
				);
	}
	
	public void testOnDynamicArrayType() throws Exception {
		String s = "void foo() { int[] x; auto y = x.";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.FIELD_REF, 
				"alignof", s.length(), s.length(),
				"dup", s.length(), s.length(),
				"init", s.length(), s.length(),
				"length", s.length(), s.length(),
				"mangleof", s.length(), s.length(),
				"ptr", s.length(), s.length(),
				"reverse", s.length(), s.length(),
				"sizeof", s.length(), s.length(),
				"sort", s.length(), s.length(),
				"stringof", s.length(), s.length()
				);
	}
	
	public void testOnStruct() throws Exception {
		String s = "struct Bar { int x; int y; void foo(int x) { } } void foo() { Bar x; auto y = x.";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.FIELD_REF, 
				"alignof", s.length(), s.length(),
				"init", s.length(), s.length(),
				"mangleof", s.length(), s.length(),
				"sizeof", s.length(), s.length(),
				"stringof", s.length(), s.length(),
				"x", s.length(), s.length(),
				"y", s.length(), s.length()
				);
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.METHOD_REF, 
				"foo()", s.length(), s.length()
				);
	}
	
	public void testOnClass() throws Exception {
		String s = "class Bar { int x; int y; void foo(int x) { } } void foo() { Bar x; auto y = x.";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.FIELD_REF, 
				"alignof", s.length(), s.length(),
				"init", s.length(), s.length(),
				"mangleof", s.length(), s.length(),
				"sizeof", s.length(), s.length(),
				"stringof", s.length(), s.length(),
				"x", s.length(), s.length(),
				"y", s.length(), s.length()
				);
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.METHOD_REF, 
				"factory()", s.length(), s.length(),
				"foo()", s.length(), s.length(),
				"notifyRegister()", s.length(), s.length(),
				"notifyUnRegister()", s.length(), s.length(),
				"opCmp()", s.length(), s.length(),
				"opEquals()", s.length(), s.length(),
				"print()", s.length(), s.length(),
				"toHash()", s.length(), s.length(),
				"toString()", s.length(), s.length()
				);
	}
	
	public void testOnClassWithBaseInterface() throws Exception {
		String s = "interface Foo { void bar(int x); } class Bar : Foo { int x; int y; void foo(int x) { } } void foo() { Bar x; auto y = x.";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.FIELD_REF, 
				"alignof", s.length(), s.length(),
				"init", s.length(), s.length(),
				"mangleof", s.length(), s.length(),
				"sizeof", s.length(), s.length(),
				"stringof", s.length(), s.length(),
				"x", s.length(), s.length(),
				"y", s.length(), s.length()
				);
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.METHOD_REF, 
				"bar()", s.length(), s.length(),
				"factory()", s.length(), s.length(),
				"foo()", s.length(), s.length(),
				"notifyRegister()", s.length(), s.length(),
				"notifyUnRegister()", s.length(), s.length(),
				"opCmp()", s.length(), s.length(),
				"opEquals()", s.length(), s.length(),
				"print()", s.length(), s.length(),
				"toHash()", s.length(), s.length(),
				"toString()", s.length(), s.length()
				);
	}
	
	public void testOnClassWithBaseInterfaceDontDuplicateMethods() throws Exception {
		String s = "interface Foo { void foo(int x); } class Bar : Foo { int x; int y; void foo(int x) { } } void foo() { Bar x; auto y = x.";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.FIELD_REF, 
				"alignof", s.length(), s.length(),
				"init", s.length(), s.length(),
				"mangleof", s.length(), s.length(),
				"sizeof", s.length(), s.length(),
				"stringof", s.length(), s.length(),
				"x", s.length(), s.length(),
				"y", s.length(), s.length()
				);
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.METHOD_REF, 
				"factory()", s.length(), s.length(),
				"foo()", s.length(), s.length(),
				"notifyRegister()", s.length(), s.length(),
				"notifyUnRegister()", s.length(), s.length(),
				"opCmp()", s.length(), s.length(),
				"opEquals()", s.length(), s.length(),
				"print()", s.length(), s.length(),
				"toHash()", s.length(), s.length(),
				"toString()", s.length(), s.length()
				);
	}

}
