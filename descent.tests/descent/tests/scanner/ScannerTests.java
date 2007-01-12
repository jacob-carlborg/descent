package descent.tests.scanner;

import descent.core.compiler.IScanner;
import descent.core.compiler.ITerminalSymbols;
import descent.core.dom.AST;
import descent.core.dom.ToolFactory;
import junit.framework.TestCase;

public class ScannerTests extends TestCase implements ITerminalSymbols {
	
	public void testSymbols() throws Exception {
		IScanner scanner = ToolFactory.createScanner(true, true, true, AST.D1);
		scanner.setSource("{}[]()*/".toCharArray());
		assertNextToken(scanner, TokenNameLCURLY, 0, 1, "{");
		assertNextToken(scanner, TokenNameRCURLY, 1, 2, "}");
		assertNextToken(scanner, TokenNameLBRACKET, 2, 3, "[");
		assertNextToken(scanner, TokenNameRBRACKET, 3, 4, "]");
		assertNextToken(scanner, TokenNameLPAREN, 4, 5, "(");
		assertNextToken(scanner, TokenNameRPAREN, 5, 6, ")");
		assertNextToken(scanner, TokenNameMULTIPLY, 6, 7, "*");
		assertNextToken(scanner, TokenNameDIVIDE, 7, 8, "/");
	}
	
	private void assertNextToken(IScanner scanner, int terminalSymbol, int start, int end, String rawSource) throws Exception {
		int token = scanner.getNextToken();
		assertEquals(terminalSymbol, token);
		assertEquals(start, scanner.getCurrentTokenStartPosition());
		assertEquals(end, scanner.getCurrentTokenEndPosition());
		assertEquals(rawSource, new String(scanner.getRawTokenSource()));
	}

}
