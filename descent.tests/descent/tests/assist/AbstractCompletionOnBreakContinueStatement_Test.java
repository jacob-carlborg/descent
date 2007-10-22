package descent.tests.assist;

import descent.core.CompletionProposal;

public abstract class AbstractCompletionOnBreakContinueStatement_Test extends AbstractCompletionTest {
	
	protected abstract String getKeyword();
	
	public void testEmptyOnFunction() throws Exception {
		String s = "void foo() { someLabel: while(true) { " + getKeyword() + " ";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.LABEL_REF, "someLabel", s.length(), s.length());
	}
	
	public void testSomeOnFunction() throws Exception {
		String s = "void foo() { someLabel: while(true) { " + getKeyword() + " s";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.LABEL_REF, "someLabel", s.length() - 1, s.length());
	}
	
	public void testEmptyOnUnitTest() throws Exception {
		String s = "unittest { someLabel: while(true) " + getKeyword() + " ";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.LABEL_REF, "someLabel", s.length(), s.length());
	}
	
	public void testSomeOnUnitTest() throws Exception {
		String s = "unittest { someLabel: while(true) { " + getKeyword() + " s";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.LABEL_REF, "someLabel", s.length() - 1, s.length());
	}
	
	public void testEmptyOnInvariant() throws Exception {
		String s = "invariant() { someLabel: while(true) { " + getKeyword() + " ";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.LABEL_REF, "someLabel", s.length(), s.length());
	}
	
	public void testSomeOnInvariant() throws Exception {
		String s = "invariant() { someLabel: while(true) { " + getKeyword() + " s";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.LABEL_REF, "someLabel", s.length() - 1, s.length());
	}
	
	public void testShowOnlyRelevantForWhile() throws Exception {
		String s = "void foo() { unrelevantLabel: int x; someLabel: while(true) { " + getKeyword() + " ";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.LABEL_REF, "someLabel", s.length(), s.length());
	}
	
	public void testShowForWhileWithCurlies() throws Exception {
		String s = "void foo() { unrelevantLabel: int x; someLabel: { while(true) { " + getKeyword() + " ";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.LABEL_REF, "someLabel", s.length(), s.length());
	}
	
	public void testShowForWhileNested() throws Exception {
		String s = "void foo() { unrelevantLabel: int x; someLabel: while(true) { otherLabel: while(true) { " + getKeyword() + " ";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.LABEL_REF, "otherLabel", s.length(), s.length(),
				CompletionProposal.LABEL_REF, "someLabel", s.length(), s.length());
	}
	
	public void testShowOnlyRelevantForDo() throws Exception {
		String s = "void foo() { unrelevantLabel: int x; someLabel: do { " + getKeyword() + " ";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.LABEL_REF, "someLabel", s.length(), s.length());
	}
	
	public void testShowForDoWithCurlies() throws Exception {
		String s = "void foo() { unrelevantLabel: int x; someLabel: { do { " + getKeyword() + " ";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.LABEL_REF, "someLabel", s.length(), s.length());
	}
	
	public void testShowOnlyRelevantForFor() throws Exception {
		String s = "void foo() { unrelevantLabel: int x; someLabel: for(;;) { " + getKeyword() + " ";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.LABEL_REF, "someLabel", s.length(), s.length());
	}
	
	public void testShowForDoWithCurliesFor() throws Exception {
		String s = "void foo() { unrelevantLabel: int x; someLabel: { for(;;) { " + getKeyword() + " ";
		
		assertCompletions(null, "test.d", s, s.length(), 
				CompletionProposal.LABEL_REF, "someLabel", s.length(), s.length());
	}

}
