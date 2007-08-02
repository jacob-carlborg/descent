package descent.tests.utils;

import descent.core.ToolFactory;
import descent.core.compiler.IScanner;
import descent.core.compiler.ITerminalSymbols;
import descent.core.dom.AST;

public class Util {
	
	public static boolean equalsTokenByToken(String document1, String document2) throws Exception {
		IScanner scanner1 = ToolFactory.createScanner(true, true, false, false, AST.D2);
		IScanner scanner2 = ToolFactory.createScanner(true, true, false, false, AST.D2);
		
		scanner1.setSource(document1.toCharArray());
		scanner2.setSource(document2.toCharArray());
		
		int token1 = scanner1.getNextToken();
		int token2 = scanner2.getNextToken();
		
		try {
			while(token1 == token2) {
				if (token1 == ITerminalSymbols.TokenNameEOF) {
					return true;
				}
				if (!equals(scanner1.getRawTokenSource(), scanner2.getRawTokenSource())) {
					return false;
				}				
				token1 = scanner1.getNextToken();
				token2 = scanner2.getNextToken();
			}
		} catch (Throwable t) {
		}
		return false;
	}
	
	public static boolean equals(char[] s1, char[] s2) {
		if (s1.length != s2.length) return false;
		for(int i = 0; i < s1.length; i++) {
			if (s1[i] != s2[i]) return false;
		}
		return true;
	}
	

}
