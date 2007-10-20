package descent.tests.assist;

import java.util.Collections;
import java.util.Comparator;

import descent.core.CompletionProposal;
import descent.core.ICompilationUnit;
import descent.tests.model.AbstractModelTest;

public abstract class AbstractCompletionTest extends AbstractModelTest {
	
	/**
	 * For example:
	 * 
	 * assertCompletions(null, "test.d", "module ", 7,
	 *     CompletionProposal.PACKAGE_REF, "test", 7, 7); 
	 *     // type, completion, replace start, replace end
	 *     // (more like the previous like can follow)
	 *     
	 * You should expect the completions in lexicograpical order.
	 */
	protected void assertCompletions(
			String packageName, 
			String filename, 
			String contents, 
			int cursorLocation,
			Object ... expectations) throws Exception {
		
		CompletionRequestorCollector req = new CompletionRequestorCollector();
		
		ICompilationUnit unit = createCompilationUnit(packageName, filename, contents);
		unit.codeComplete(cursorLocation, req);
		
		Collections.sort(req.proposals, new Comparator<CompletionProposal>() {

			public int compare(CompletionProposal o1, CompletionProposal o2) {
				return new String(o1.getCompletion()).compareTo(new String(o2.getCompletion()));
			}
			
		});
		
		int num = 0;
		for(int i = 0; i < expectations.length; i++) {
			assertTrue(num < req.proposals.size());
			CompletionProposal prop = req.proposals.get(num);
			
			assertTrue(expectations[i] instanceof Integer);
			
			int type = (Integer) expectations[i];
			assertEquals(type, prop.getKind());
			
			i++;
			assertTrue(expectations[i] instanceof String);
			String completion = (String) expectations[i];
			assertEquals(completion, new String(prop.getCompletion()));
			
			i++;
			assertTrue(expectations[i] instanceof Integer);
			int start = (Integer) expectations[i];
			assertEquals(start, prop.getReplaceStart());
			
			i++;
			assertTrue(expectations[i] instanceof Integer);
			int end = (Integer) expectations[i];
			assertEquals(end, prop.getReplaceEnd());
			
			num++;
		}
		
		// No more proposals
		assertEquals(num, req.proposals.size());
	}

}
