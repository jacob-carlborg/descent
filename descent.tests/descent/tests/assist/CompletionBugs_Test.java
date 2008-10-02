package descent.tests.assist;

import descent.core.CompletionProposal;

public class CompletionBugs_Test extends AbstractCompletionTest {
	
	public void testTicket92() throws Exception {
		String s = 
				"interface I {\n" + 
				"	void run();\n" + 
				"}\n" + 
				"\n" + 
				"void doIt() {\n" + 
				"	void doIt2(I i) {\n" + 
				"		i.r\n" + 
				"	}\n" + 
				"}";
		
		int loc = s.lastIndexOf("i.r") + 3;
		
		assertCompletions(null, "test.d", s, loc, CompletionProposal.METHOD_REF,
				"run()", loc - 1, loc);
	}

}
