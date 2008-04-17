package descent.tests.assist;

import descent.core.CompletionProposal;

public class OpCallProposal_Test extends AbstractCompletionTest {
	
	public void testClassStaticOpCallsInStatement() throws Exception {
		String s = "class Foo { static Foo opCall(int x) { } static Foo opCall(int x, int y) { } } void foo() { Foo }";
		
		int pos = s.lastIndexOf("Foo") + 3; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.OP_CALL,
				new int[] { LABEL },
				"Foo()", pos - 3, pos, "Foo(int x)  Foo - Foo",
				"Foo()", pos - 3, pos, "Foo(int x, int y)  Foo - Foo"
				);
	}
	
	public void testClassStaticOpCallsInExpression() throws Exception {
		String s = "class Foo { static Foo opCall(int x) { } static Foo opCall(int x, int y) { } } void foo() { Foo foo = Foo }";
		
		int pos = s.lastIndexOf("Foo") + 3; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.OP_CALL,
				new int[] { LABEL },
				"Foo()", pos - 3, pos, "Foo(int x)  Foo - Foo",
				"Foo()", pos - 3, pos, "Foo(int x, int y)  Foo - Foo"
				);
	}
	
	public void testDontSuggestClassStaticOpCallsInNew() throws Exception {
		String s = "class Foo { static Foo opCall(int x) { } static Foo opCall(int x, int y) { } } void foo() { Foo foo = new Foo }";
		
		int pos = s.lastIndexOf("Foo") + 3; 
		
		assertCompletions(null, "test.d", s, pos, CompletionProposal.OP_CALL);
	}

}
