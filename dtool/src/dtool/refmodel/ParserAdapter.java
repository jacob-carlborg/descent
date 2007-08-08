package dtool.refmodel;

import descent.internal.core.dom.Parser;
import descent.internal.core.dom.ParserFacade;
import descent.internal.core.dom.TOK;
import descent.internal.core.dom.Token;

public class ParserAdapter {

	Parser parser;
	
	/** The error message or null if no error. */
	protected String error = null;
	/** Whether as qualified dot fix was performed */
	protected boolean isQualifiedDotFixSearch = false;

	public ParserAdapter(Parser parser) {
		this.parser = parser;
	}

	/** Attempt syntax recovery for the purposes of code completion. */
	public static ParserAdapter recoverForCompletion(Parser parser, String str,
			int offset, Token lastToken) {
		ParserAdapter adapter = new ParserAdapter(parser);
		adapter.internalRecover(str, offset, lastToken);
		return adapter;
	}

	private void internalRecover(String str, int offset, Token lastToken) {
		if(parser.mod == null) {
			parser = ParserFacade.parseCompilationUnit(str);
		}
		
		if(parser.mod.getProblems().length != 0) {
			if(lastToken != null && lastToken.value == TOK.TOKdot) {
				
				// Insert a dummy identifier, so the reference will parse
				String newstr = str.substring(0, offset) + "_" 
					+ str.substring(offset, str.length());
				
				parser = ParserFacade.parseCompilationUnit(newstr);
				
				if(parser.mod.getProblems().length == 0) {
					// We succeeded, mark this for ahead
					isQualifiedDotFixSearch = true;
					return;
				}
				error = "Syntax Errors, cannot complete. (even with dot recovery)";
				
			} else {
				error = "Syntax Errors, cannot complete.";
			}
		}
		
	}

}
