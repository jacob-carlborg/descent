package descent.internal.compiler.parser;

import org.eclipse.osgi.util.NLS;

final class ProblemMessages extends NLS {

	private static final String BUNDLE_NAME= ProblemMessages.class.getName(); 

	private ProblemMessages() {
		// Do not instantiate
	}
	
	/* EVAL-FOR-EACH
	 * 
	 * print DST "\tpublic static String " . $$_{'optName'} . ";\n";
	 *
	 */
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, ProblemMessages.class);
	}
}
