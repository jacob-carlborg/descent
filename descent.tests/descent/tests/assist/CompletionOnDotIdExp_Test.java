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

}
