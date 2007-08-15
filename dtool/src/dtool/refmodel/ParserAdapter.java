package dtool.refmodel;

import descent.core.dom.AST;
import descent.internal.compiler.parser.Lexer;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.Token;

public class ParserAdapter {

	public Module mod;
	Parser parser;
	
	/** The error message or null if no error. */
	protected String error = null;
	/** Whether as qualified dot fix was performed */
	protected boolean isQualifiedDotFixSearch = false;

	public ParserAdapter(Parser parser) {
		this.parser = parser;
	}
	
	public static Token tokenizeSource(String str) {
		Token tokenList = null; 
		Lexer lexer = new Lexer(str, 0, str.length(), false, false, false, false, AST.D2);
		do {
			lexer.nextToken();
		    Token newtoken = new Token(lexer.token);
			if(tokenList != null) {
				tokenList.next = newtoken; 
			} else {
				tokenList = newtoken;
			}
			tokenList = newtoken;
		} while(tokenList.value != TOK.TOKeof);
		return tokenList;
	}
	
	public static ParserAdapter parseSource(String str) {
		Parser newparser = new Parser(AST.newAST(AST.D2), str);
		ParserAdapter adapter = new ParserAdapter(newparser);
		adapter.mod = adapter.parser.parseModuleObj();
		return adapter;
	}
	

	/** Attempt syntax recovery for the purposes of code completion. */
	public void recoverForCompletion(String str, int offset, Token lastToken) {
		internalRecover(str, offset, lastToken);
	}

	public void parseModule(String str) {
		parser = new Parser(null, str);
		mod = parser.parseModuleObj();
	}
	
	private void internalRecover(String str, int offset, Token lastToken) {
		
		if(mod == null) {
			parseModule(str);
		}
		
		if(mod.problems.size() != 0) {
			if(lastToken != null && lastToken.value == TOK.TOKdot) {
				
				// Insert a dummy identifier, so the reference will parse
				String newstr = str.substring(0, offset) + "_" 
					+ str.substring(offset, str.length());
				
				parseModule(newstr);
				
				if(mod.problems.size() == 0) {
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
