package descent.tests.rewrite;

import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import descent.core.compiler.IScanner;
import descent.core.compiler.ITerminalSymbols;
import descent.core.dom.AST;
import descent.core.dom.CompilationUnit;
import descent.core.dom.ToolFactory;
import descent.tests.mars.Parser_Test;

public abstract class RewriteTest extends Parser_Test {
	
	protected CompilationUnit unit;
	protected Document document;
	protected AST ast;
	
	protected void begin(String source) {
		document = new Document(source);
		unit = getCompilationUnit(document.get());
		unit.recordModifications();
		ast = unit.getAST();
	}
	
	protected String end() throws Exception {
		TextEdit edit = unit.rewrite(document, null);
		edit.apply(document);		
		return document.get().trim();
	}
	
	protected void assertEqualsTokenByToken(String document1, String document2) throws Exception {
		IScanner scanner1 = ToolFactory.createScanner(true, true, false, false, AST.D1);
		IScanner scanner2 = ToolFactory.createScanner(true, true, false, false, AST.D1);
		
		scanner1.setSource(document1.toCharArray());
		scanner2.setSource(document2.toCharArray());
		
		int token1 = scanner1.getNextToken();
		int token2 = scanner2.getNextToken();
		
		try {
			while(token1 == token2) {
				if (token1 == ITerminalSymbols.TokenNameEOF) {
					return;
				}
				assertEquals(scanner1.getRawTokenSource(), scanner2.getRawTokenSource());
				
				token1 = scanner1.getNextToken();
				token2 = scanner2.getNextToken();
			}
		} catch (Throwable t) {
		}
		fail("'" + document1 + "' is not equal to '" + document2 + "'");
	}
	
	protected void assertEquals(char[] s1, char[] s2) {
		assertEquals(s1.length, s2.length);
		for(int i = 0; i < s1.length; i++) {
			assertEquals(s1[i], s2[i]);
		}
	}

}
