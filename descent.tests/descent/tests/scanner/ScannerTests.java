package descent.tests.scanner;

import junit.framework.TestCase;
import descent.core.ToolFactory;
import descent.core.compiler.IScanner;
import descent.core.compiler.ITerminalSymbols;
import descent.core.dom.AST;
import descent.internal.core.parser.TOK;

public class ScannerTests extends TestCase implements ITerminalSymbols {
	
	public void testEasy() throws Throwable {
		for(TOK tok : TOK.values()) {
			switch(tok) {
			case TOKreserved:
			case TOKeof:
			case TOKlinecomment:
			case TOKdoclinecomment:
			case TOKblockcomment:
			case TOKdocblockcomment:
			case TOKpluscomment:
			case TOKdocpluscomment:
			case TOKint32v:
			case TOKuns32v:
			case TOKint64v:
			case TOKuns64v:
			case TOKfloat32v:
			case TOKfloat64v:
			case TOKfloat80v:
			case TOKimaginary32v:
			case TOKimaginary64v:
			case TOKimaginary80v:
			case TOKcharv:
			case TOKwcharv:
			case TOKdcharv:
			case TOKidentifier:
			case TOKstring:
			case TOKwhitespace:
			case TOKPRAGMA:
				continue;
			}
			
			IScanner scanner = ToolFactory.createScanner(true, true, true, true, AST.LATEST);
			scanner.setSource(("$" + tok.value).toCharArray());
			assertNextToken(scanner, TokenNameDOLLAR, 0, 0, "$");
			try {
				assertNextToken(scanner, tok.terminalSymbol, 1, tok.value.length(), tok.value);
			} catch (Throwable e) {
				fail(tok.toString());
				throw e;
			}
		}
	}
	
	public void testEOF() throws Throwable {
		IScanner scanner = ToolFactory.createScanner(true, true, true, true, AST.LATEST);
		scanner.setSource("$".toCharArray());
		assertNextToken(scanner, TokenNameDOLLAR, 0, 0, "$");
		assertNextToken(scanner, TokenNameEOF, 1, 1, "");
	}
	
	public void testLiterals() throws Throwable {
		Object[][] pairs = {
			{ "1", TokenNameIntegerLiteral },	
			{ "1u", TokenNameUnsignedIntegerLiteral },
			{ "1L", TokenNameLongLiteral },			
			{ "1uL", TokenNameUnsignedLongLiteral },
			{ "1.2f", TokenNameFloatLiteral },
			{ "1.2", TokenNameDoubleLiteral },
			{ "1.2L", TokenNameRealLiteral },
			{ "1.2fi", TokenNameImaginaryFloatLiteral },
			{ "1.2i", TokenNameImaginaryDoubleLiteral },
			{ "1.2Li", TokenNameImaginaryRealLiteral },
			{ "'c'", TokenNameCharacterLiteral },
			{ "'\\u1234'", TokenNameWCharacterLiteral },
			{ "'\\U00001234'", TokenNameDCharacterLiteral },			
			{ "identifier", TokenNameIdentifier },
			{ "\"some string\"", TokenNameStringLiteral },
			{ "r\"some string\"", TokenNameStringLiteral },
			{ "`some string`", TokenNameStringLiteral },
		};
		
		for(Object[] pair : pairs) {
			String value = (String) pair[0];
			int terminalSymbol = (Integer) pair[1];
			
			IScanner scanner = ToolFactory.createScanner(true, true, true, true, AST.LATEST);
			scanner.setSource(("$" + value).toCharArray());
			assertNextToken(scanner, TokenNameDOLLAR, 0, 0, "$");
			try {
				assertNextToken(scanner, terminalSymbol, 1, value.length(), value);
			} catch (Throwable e) {
				fail(value);
				throw e;
			}
		}
	}
	
	public void testComments() throws Throwable {
		Object[][] pairs = {
			{ 	"/* hola */", TokenNameCOMMENT_BLOCK },
			{ 	"/** hola */", TokenNameCOMMENT_DOC_BLOCK },
			{ 	"// hola", TokenNameCOMMENT_LINE },
			{ 	"/// hola", TokenNameCOMMENT_DOC_LINE },
			{ 	"/+ hola +/", TokenNameCOMMENT_PLUS },
			{ 	"/++ hola +/", TokenNameCOMMENT_DOC_PLUS },
		};
		
		for(Object[] pair : pairs) {
			String comment = (String) pair[0];
			int terminalSymbol = (Integer) pair[1];
			
			IScanner scanner = ToolFactory.createScanner(true, true, true, true, AST.LATEST);
			scanner.setSource(("$" + comment).toCharArray());
			assertNextToken(scanner, TokenNameDOLLAR, 0, 0, "$");
			try {
				assertNextToken(scanner, terminalSymbol, 1, comment.length(), comment);
			} catch (Throwable e) {
				fail(comment);
				throw e;
			}
		}
	}
	
	public void testWhitespace() throws Throwable {
		for(String white : new String[] { 
				" ", 
				"\n  \r", 
				"\r   \n", 
				"\f   \t", 
				"      ", 
				"\r\n",
			}) {
			IScanner scanner = ToolFactory.createScanner(true, true, true, true, AST.LATEST);
			scanner.setSource(("$" + white).toCharArray());
			assertNextToken(scanner, TokenNameDOLLAR, 0, 0, "$");
			assertNextToken(scanner, ITerminalSymbols.TokenNameWHITESPACE, 1, white.length(), white);
		}
	}
	
	public void testPragma() throws Throwable {
		Object[][] pairs = {
				{ 	"#! script", TokenNamePRAGMA },
				{ 	"# something", TokenNamePRAGMA },
			};
			
			for(Object[] pair : pairs) {
				String comment = (String) pair[0];
				int terminalSymbol = (Integer) pair[1];
				
				IScanner scanner = ToolFactory.createScanner(true, true, true, true, AST.LATEST);
				scanner.setSource(("$" + comment).toCharArray());
				assertNextToken(scanner, TokenNameDOLLAR, 0, 0, "$");
				try {
					assertNextToken(scanner, terminalSymbol, 1, comment.length(), comment);
				} catch (Throwable e) {
					fail(comment);
					throw e;
				}
			}
	}
	
	public void testPragmaRN() throws Throwable {
		IScanner scanner = ToolFactory.createScanner(true, true, true, true, AST.LATEST);
		scanner.setSource(("$# something\r\n").toCharArray());
		assertNextToken(scanner, TokenNameDOLLAR, 0, 0, "$");
		assertNextToken(scanner, ITerminalSymbols.TokenNamePRAGMA, 1, 11, "# something");
	}
	
	public void testEndLines() throws Throwable {
		IScanner scanner = ToolFactory.createScanner(true, true, true, true, AST.LATEST);
		scanner.setSource((
				"int x = 2;\n" +
				"int bla = 3;\n" +
				"int lala = 4;\r\n" +
				"int"
				).toCharArray()
			);
		assertEquals(0, scanner.getLineEnds().length);
		
		while(scanner.getNextToken() != ITerminalSymbols.TokenNameEOF) {
		}
		
		assertEquals(3, scanner.getLineEnds().length);
		
		assertEquals(0, scanner.getLineStart(1));
		assertEquals(10, scanner.getLineEnd(1));
		
		assertEquals(11, scanner.getLineStart(2));
		assertEquals(23, scanner.getLineEnd(2));
		
		assertEquals(24, scanner.getLineStart(3));
		assertEquals(38, scanner.getLineEnd(3));
		
		assertEquals(39, scanner.getLineStart(4));
		assertEquals(42, scanner.getLineEnd(4));
		
		for(int i = 0; i <= 10; i++) {
			assertEquals(1, scanner.getLineNumber(i));
		}
		for(int i = 11; i <= 23; i++) {
			assertEquals(2, scanner.getLineNumber(i));
		}
		for(int i = 24; i <= 38; i++) {
			assertEquals(3, scanner.getLineNumber(i));
		}
		for(int i = 39; i <= 41; i++) {
			assertEquals(4, scanner.getLineNumber(i));
		}
	}
	
	private void assertNextToken(IScanner scanner, int terminalSymbol, int start, int end, String rawSource) throws Exception {
		int token = scanner.getNextToken();
		assertEquals(terminalSymbol, token);
		assertEquals(start, scanner.getCurrentTokenStartPosition());
		assertEquals(end, scanner.getCurrentTokenEndPosition());
		assertEquals(rawSource, new String(scanner.getRawTokenSource()));
	}

}

