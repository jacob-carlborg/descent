package descent.tests.assist;

import descent.core.CompletionProposal;

public class CompletionOnGotoStatement_Test extends AbstractCompletionTest {
	
	public void testEmptyOnFunction() throws Exception {
		String s = "void foo() { someLabel: int x; otherLabel: int y; goto ";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.LABEL_REF, "otherLabel", s.length(), s.length(),
				CompletionProposal.LABEL_REF, "someLabel", s.length(), s.length());
	}
	
	public void testSomeOnFunction() throws Exception {
		String s = "void foo() { someLabel: int x; otherLabel: int y; goto o";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.LABEL_REF, "otherLabel", s.length() - 1, s.length());
	}
	
	public void testEmptyOnUnitTest() throws Exception {
		String s = "unittest { someLabel: int x; otherLabel: int y; goto ";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.LABEL_REF, "otherLabel", s.length(), s.length(),
				CompletionProposal.LABEL_REF, "someLabel", s.length(), s.length());
	}
	
	public void testSomeOnUnitTest() throws Exception {
		String s = "unittest { someLabel: int x; otherLabel: int y; goto o";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.LABEL_REF, "otherLabel", s.length() - 1, s.length());
	}
	
	public void testEmptyOnInvariant() throws Exception {
		String s = "invariant() { someLabel: int x; otherLabel: int y; goto ";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.LABEL_REF, "otherLabel", s.length(), s.length(),
				CompletionProposal.LABEL_REF, "someLabel", s.length(), s.length());
	}
	
	public void testSomeOnInvariant() throws Exception {
		String s = "invariant() { someLabel: int x; otherLabel: int y; goto o";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.LABEL_REF, "otherLabel", s.length() - 1, s.length());
	}

}
