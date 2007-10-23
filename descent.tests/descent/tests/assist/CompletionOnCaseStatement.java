package descent.tests.assist;

import descent.core.CompletionProposal;

public class CompletionOnCaseStatement extends AbstractCompletionTest {
	
	public void testEmpty() throws Exception {
		String s = "enum Foo { a, b, c } void foo() { Foo f = Foo.a; switch(f) { case ";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.FIELD_REF, "Foo.a", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "Foo.b", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "Foo.c", s.length(), s.length());
	}
	
	public void testSome() throws Exception {
		String s = "enum Foo { a, b, c } void foo() { Foo f = Foo.a; switch(f) { case F";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.FIELD_REF, "Foo.a", s.length() - 1, s.length(),
				CompletionProposal.FIELD_REF, "Foo.b", s.length() - 1, s.length(),
				CompletionProposal.FIELD_REF, "Foo.c", s.length() - 1, s.length());
	}
	
	public void testEmptyWithBaseTypeNotInt() throws Exception {
		String s = "enum Foo : char { a, b, c } void foo() { Foo f = Foo.a; switch(f) { case ";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.FIELD_REF, "Foo.a", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "Foo.b", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "Foo.c", s.length(), s.length());
	}
	
	public void testSomeWithBaseTypeNotInt() throws Exception {
		String s = "enum Foo : char { a, b, c } void foo() { Foo f = Foo.a; switch(f) { case F";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.FIELD_REF, "Foo.a", s.length() - 1, s.length(),
				CompletionProposal.FIELD_REF, "Foo.b", s.length() - 1, s.length(),
				CompletionProposal.FIELD_REF, "Foo.c", s.length() - 1, s.length());
	}
	
	public void testEmptyExcludeAlreadyUsed() throws Exception {
		String s = "enum Foo : char { a, b, c } void foo() { Foo f = Foo.a; switch(f) { case Foo.a: break; case ";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.FIELD_REF, "Foo.b", s.length(), s.length(),
				CompletionProposal.FIELD_REF, "Foo.c", s.length(), s.length());
	}
	
	public void testSomeExcludeAlreadyUsed() throws Exception {
		String s = "enum Foo : char { a, b, c } void foo() { Foo f = Foo.a; switch(f) { case Foo.a: break; case f";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.FIELD_REF, "Foo.b", s.length() - 1, s.length(),
				CompletionProposal.FIELD_REF, "Foo.c", s.length() - 1, s.length());
	}

}
