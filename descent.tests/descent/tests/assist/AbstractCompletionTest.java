package descent.tests.assist;

import java.util.Collections;
import java.util.Comparator;

import descent.core.CompletionProposal;
import descent.core.ICompilationUnit;
import descent.tests.model.AbstractModelTest;
import descent.ui.text.java.CompletionProposalLabelProvider;

public abstract class AbstractCompletionTest extends AbstractModelTest {
	
	public final static int LABEL = 1;
	public final static int SIGNATURE = 2;
	public final static int TYPE_SIGNATURE = 3;
	public final static int DECLARATION_SIGNATURE = 4;
	
	CompletionProposalLabelProvider labelProvider = new CompletionProposalLabelProvider();
	
	/**
	 * For example:
	 * 
	 * assertCompletions(
	 * 		null, "test.d", "module ", 7, CompletionProposal.PACKAGE_REF, 
	 * 		"test", 7, 7); 
	 *   // completion, replace start, replace end
	 *   // (more like the previous like can follow)
	 *     
	 * You should expect the completions in lexicograpical order.
	 */
	protected void assertCompletions(
			String packageName, 
			String filename, 
			String contents,
			int cursorLocation,
			int kind,
			Object ... expectations) throws Exception {
		assertCompletions(packageName, filename, contents, cursorLocation, kind, new int[0], expectations);
	}
	
	/**
	 * For example:
	 * 
	 * assertCompletions(
	 * 		null, "test.d", "module ", 7, 
	 * 
	 * 		CompletionProposal.PACKAGE_REF, new int[] { SIGNATURE, LABEL }	 *    
	 * 
	 * 		"test", 7, 7, "@4test", "test"); 
	 *   // completion, replace start, replace end, extras
	 *   // (more like the previous like can follow)
	 *     
	 * You should expect the completions in lexicograpical order.
	 */
	protected void assertCompletions(
			String packageName, 
			String filename, 
			String contents,
			int cursorLocation,
			int kind,
			int[] extras,
			Object ... expectations) throws Exception {
		
		CompletionRequestorCollector req = new CompletionRequestorCollector(kind);
		
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
			
			for (int j = 0; j < extras.length; j++, i++) {
				switch(extras[i]) {
				case LABEL:
					assertTrue(expectations[i] instanceof String);
					String label = (String) expectations[i];					
					assertEquals(label, labelProvider.createLabel(prop));
					break;
				case SIGNATURE:
					assertTrue(expectations[i] instanceof String);
					String signature = (String) expectations[i];
					assertEquals(signature, new String(prop.getSignature()));
					break;
				case TYPE_SIGNATURE:
					assertTrue(expectations[i] instanceof String);
					String typeSignature = (String) expectations[i];
					assertEquals(typeSignature, new String(prop.getTypeSignature()));
					break;
				case DECLARATION_SIGNATURE:
					assertTrue(expectations[i] instanceof String);
					String declSignature = (String) expectations[i];
					assertEquals(declSignature, new String(prop.getDeclarationSignature()));
					break;
				}
			}
			
			num++;
		}
		
		// No more proposals
		assertEquals(num, req.proposals.size());
	}

}
