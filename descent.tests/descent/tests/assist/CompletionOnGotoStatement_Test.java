package descent.tests.assist;

import descent.core.CompletionProposal;

public class CompletionOnGotoStatement_Test extends AbstractCompletionTest {
	
	public void testEmpty() throws Exception {
		String s = "void foo() { someLabel: int x; otherLabel: int y; goto ";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.LABEL_REF, "otherLabel", s.length(), s.length(),
				CompletionProposal.LABEL_REF, "someLabel", s.length(), s.length());
	}
	
	public void testSome() throws Exception {
		String s = "void foo() { someLabel: int x; otherLabel: int y; goto o";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.LABEL_REF, "otherLabel", s.length() - 1, s.length());
	}

}
