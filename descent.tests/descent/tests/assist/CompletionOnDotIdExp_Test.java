package descent.tests.assist;

import descent.core.CompletionProposal;

public class CompletionOnDotIdExp_Test extends AbstractCompletionTest {
	
	public void testOnEnum() throws Exception {
		String s = "enum X { a, b, c } void foo() { int x = X.";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.FIELD_REF, "a", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "alignof", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "b", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "c", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "init", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "mangleof", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "max", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "min", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "sizeof", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "stringof", s.length(), s.length()
				);
	}
	
	public void testOnEnumWithSome() throws Exception {
		String s = "enum X { a, b, c } void foo() { int x = X.a";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.FIELD_REF, "a", s.length() - 1, s.length(),
				CompletionProposal.FIELD_REF, "alignof", s.length() - 1, s.length()
				);
	}
	
	public void testOnStaticArrayType() throws Exception {
		String s = "void foo() { int[3] x; auto y = x.";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.FIELD_REF, "alignof", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "dup", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "init", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "length", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "mangleof", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "ptr", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "reverse", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "sizeof", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "sort", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "stringof", s.length(), s.length()
				);
	}
	
	public void testOnDynamicArrayType() throws Exception {
		String s = "void foo() { int[] x; auto y = x.";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.FIELD_REF, "alignof", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "dup", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "init", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "length", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "mangleof", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "ptr", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "reverse", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "sizeof", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "sort", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "stringof", s.length(), s.length()
				);
	}
	
	public void testOnStruct() throws Exception {
		String s = "struct Bar { int x; int y; void foo(int x) { } } void foo() { Bar x; auto y = x.";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.FIELD_REF, "alignof", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "foo()", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "init", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "mangleof", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "sizeof", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "stringof", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "x", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "y", s.length(), s.length()
				);
	}
	
	public void testOnClass() throws Exception {
		String s = "class Bar { int x; int y; void foo(int x) { } } void foo() { Bar x; auto y = x.";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.FIELD_REF, "alignof", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "factory()", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "foo()", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "init", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "mangleof", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "notifyRegister()", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "notifyUnRegister()", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "opCmp()", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "opEquals()", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "print()", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "sizeof", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "stringof", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "toHash()", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "toString()", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "x", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "y", s.length(), s.length()
				);
	}
	
	public void testOnClassWithBaseInterface() throws Exception {
		String s = "interface Foo { void bar(int x); } class Bar : Foo { int x; int y; void foo(int x) { } } void foo() { Bar x; auto y = x.";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.FIELD_REF, "alignof", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "bar()", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "factory()", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "foo()", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "init", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "mangleof", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "notifyRegister()", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "notifyUnRegister()", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "opCmp()", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "opEquals()", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "print()", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "sizeof", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "stringof", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "toHash()", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "toString()", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "x", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "y", s.length(), s.length()
				);
	}
	
	public void testOnClassWithBaseInterfaceDontDuplicateMethods() throws Exception {
		String s = "interface Foo { void foo(int x); } class Bar : Foo { int x; int y; void foo(int x) { } } void foo() { Bar x; auto y = x.";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.FIELD_REF, "alignof", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "factory()", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "foo()", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "init", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "mangleof", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "notifyRegister()", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "notifyUnRegister()", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "opCmp()", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "opEquals()", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "print()", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "sizeof", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "stringof", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "toHash()", s.length(), s.length(),
				CompletionProposal.METHOD_REF, "toString()", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "x", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "y", s.length(), s.length()
				);
	}

}
