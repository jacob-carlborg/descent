package descent.tests.mars;

import junit.framework.TestCase;
import descent.core.dom.AST;
import descent.internal.compiler.parser.Lexer;
import descent.internal.compiler.parser.TOK;

public class Lexer_Test extends TestCase {
	
	public void testIdentifiers() {
		assertToken(" hola", TOK.TOKidentifier, 1, 4);
		assertToken(" _bien", TOK.TOKidentifier, 1, 5);
		assertToken(" ___", TOK.TOKidentifier, 1, 3);
	}
	
	public void testOperators() {
		assertToken(" .", TOK.TOKdot, 1, 1);
		assertToken(" ..", TOK.TOKslice, 1, 2);
		assertToken(" ...", TOK.TOKdotdotdot, 1, 3);
		
		assertToken(" /", TOK.TOKdiv, 1, 1);
		assertToken(" /=", TOK.TOKdivass, 1, 2);
		
		assertToken(" *", TOK.TOKmul, 1, 1);
		assertToken(" *=", TOK.TOKmulass, 1, 2);
		
		assertToken(" %", TOK.TOKmod, 1, 1);
		assertToken(" %=", TOK.TOKmodass, 1, 2);
		
		assertToken(" ^", TOK.TOKxor, 1, 1);
		assertToken(" ^=", TOK.TOKxorass, 1, 2);
		
		assertToken(" &", TOK.TOKand, 1, 1);
		assertToken(" &&", TOK.TOKandand, 1, 2);
		assertToken(" &=", TOK.TOKandass, 1, 2);
		
		assertToken(" |", TOK.TOKor, 1, 1);
		assertToken(" ||", TOK.TOKoror, 1, 2);
		assertToken(" |=", TOK.TOKorass, 1, 2);
		
		assertToken(" -", TOK.TOKmin, 1, 1);
		assertToken(" --", TOK.TOKminusminus, 1, 2);
		assertToken(" -=", TOK.TOKminass, 1, 2);
		
		assertToken(" +", TOK.TOKadd, 1, 1);
		assertToken(" ++", TOK.TOKplusplus, 1, 2);
		assertToken(" +=", TOK.TOKaddass, 1, 2);
		
		assertToken(" <", TOK.TOKlt, 1, 1);
		assertToken(" <=", TOK.TOKle, 1, 2);
		assertToken(" <>", TOK.TOKlg, 1, 2);
		assertToken(" <<", TOK.TOKshl, 1, 2);
		assertToken(" <<=", TOK.TOKshlass, 1, 3);
		assertToken(" <>=", TOK.TOKleg, 1, 3);
		
		assertToken(" >", TOK.TOKgt, 1, 1);
		assertToken(" >=", TOK.TOKge, 1, 2);
		assertToken(" >>", TOK.TOKshr, 1, 2);
		assertToken(" >>>", TOK.TOKushr, 1, 3);
		assertToken(" >>=", TOK.TOKshrass, 1, 3);
		assertToken(" >>>=", TOK.TOKushrass, 1, 4);
		
		assertToken(" !", TOK.TOKnot, 1, 1);
		assertToken(" !=", TOK.TOKnotequal, 1, 2);
		assertToken(" !<", TOK.TOKuge, 1, 2);
		assertToken(" !>", TOK.TOKule, 1 ,2);
		assertToken(" <>=", TOK.TOKleg, 1 ,3);
		assertToken(" !==", TOK.TOKnotidentity, 1, 3, AST.D0);
		assertToken(" !<>", TOK.TOKue, 1, 3);
		assertToken(" !<=", TOK.TOKug, 1, 3);
		assertToken(" !>=", TOK.TOKul, 1, 3);
		assertToken(" !<>=", TOK.TOKunord, 1, 4);
		
		assertToken(" =", TOK.TOKassign, 1, 1);
		assertToken(" ==", TOK.TOKequal, 1, 2);
		assertToken(" ===", TOK.TOKidentity, 1, 3, AST.D0);
		
		assertToken(" ~", TOK.TOKtilde, 1, 1);
		assertToken(" ~=", TOK.TOKcatass, 1, 2);
		
		assertToken(" (", TOK.TOKlparen, 1, 1);
		assertToken(" )", TOK.TOKrparen, 1, 1);
		assertToken(" [", TOK.TOKlbracket, 1, 1);
		assertToken(" ]", TOK.TOKrbracket, 1, 1);
		assertToken(" {", TOK.TOKlcurly, 1, 1);
		assertToken(" }", TOK.TOKrcurly, 1, 1);
		assertToken(" ?", TOK.TOKquestion, 1, 1);
		assertToken(" ,", TOK.TOKcomma, 1, 1);
		assertToken(" ;", TOK.TOKsemicolon, 1, 1);
		assertToken(" :", TOK.TOKcolon, 1, 1);
		assertToken(" $", TOK.TOKdollar, 1, 1);
	}
	
	public void testEscapeSequenceSome() {
		assertStringToken(" \\\\", 1, 2);
		assertStringToken(" \\\"", 1, 2);
		assertStringToken(" \\'", 1, 2);
		assertStringToken(" \\?", 1, 2);
	}
	
	public void testEscapeSequenceHex() {
		assertToken(" \\xAB", TOK.TOKstring, 1, 4);
		assertToken(" \\1", TOK.TOKstring, 1, 2);
		assertToken(" \\12", TOK.TOKstring, 1, 3);
		assertToken(" \\123", TOK.TOKstring, 1, 4);
		assertToken(" \\u1234", TOK.TOKstring, 1, 6);
		assertToken(" \\U00001234", TOK.TOKstring, 1, 10);
	}
	
	public void testString() {
		assertStringToken(" \"hola\"", 1, 6);
		assertStringToken(" \"hola\n\"", 1, 7);
		assertStringToken(" \"hola\"c", 1, 7);
		assertStringToken(" `ho\\la`", 1, 7);
		assertStringToken(" r\"ho\\la\"", 1, 8);
		assertStringToken(" `ho\\la`c", 1, 8);
		assertStringToken(" r\"ho\\la\"c", 1, 9);
		assertStringToken(" x\"1234\"", 1, 7);
		assertStringToken(" x\"1D34 ab34 c2\"", 1, 15);
		assertStringToken(" x\"1234\"c", 1, 8);
		assertStringToken(" x\"1D34 ab34 c2\"c", 1, 16);
	}
	
	public void testNumber() {
		assertInt32Token(" 1", 1, 1, 1);
		assertInt32Token(" 123_456_789", 123456789, 1, 11);
		assertInt32Token(" 1_2_3_4_5_6_", 123456, 1, 12);
		assertInt64Token(" 789L", 789, 1, 4);
		assertUns32Token(" 789u", 789, 1, 4);
		assertUns32Token(" 789U", 789, 1, 4);
		assertToken(" 123_456.567_8", TOK.TOKfloat64v, 1, 13);
		assertToken(" 1_2_3_4_5_6_._5_6_7_8", TOK.TOKfloat64v, 1, 21);
		assertToken(" 1_2_3_4_5_6_._5e-6_", TOK.TOKfloat64v, 1, 19);
		assertToken(" 0x1.FFFFFFFFFFFFFp1023", TOK.TOKfloat64v, 1, 22);
		assertToken(" 0x1p-52", TOK.TOKfloat64v, 1, 7);
		assertToken(" 0b1010", TOK.TOKint32v, 1, 6);
		assertToken(" 01234", TOK.TOKint32v, 1, 5);
		assertToken(" 0.", TOK.TOKfloat64v, 1, 2);
		assertToken(" 1.", TOK.TOKfloat64v, 1, 2);
		assertToken(" 1.175494351e-38F", TOK.TOKfloat32v, 1, 16);
		assertToken(" .175494351e-38F", TOK.TOKfloat32v, 1, 15);
		assertToken(" 6.3i", TOK.TOKimaginary64v, 1, 4);
		assertToken(" 6.3fi", TOK.TOKimaginary32v, 1, 5);
		assertToken(" 6.3Li", TOK.TOKimaginary80v, 1, 5);
	}
	
	public void testChar() {
		assertCharToken(" 'c'", TOK.TOKcharv, 1, 3);
		assertCharToken(" '\\n'", TOK.TOKcharv, 1, 4);
		assertCharToken(" '\\123'", TOK.TOKcharv, 1, 6);
		assertCharToken(" '\\u1234'", TOK.TOKwcharv, 1, 8);
		assertCharToken(" '\\U00001234'", TOK.TOKdcharv, 1, 12);
	}
	
	public void testComments() {
		assertComment(" //hola\n", "//hola", 1, 6, TOK.TOKlinecomment);
		assertComment(" ///hola\n", "///hola", 1, 7, TOK.TOKdoclinecomment);
		assertComment(" /*hola*/", "/*hola*/", 1, 8, TOK.TOKblockcomment);
		assertComment(" /**hola*/", "/**hola*/", 1, 9, TOK.TOKdocblockcomment);
		assertComment(" /+hola+/", "/+hola+/", 1, 8, TOK.TOKpluscomment);
		assertComment(" /++hola+/", "/++hola+/", 1, 9, TOK.TOKdocpluscomment);
		assertComment(" /++ /+ hola +/ +/", "/++ /+ hola +/ +/", 1, 17, TOK.TOKdocpluscomment);
	}
	
	private void assertToken(String s, TOK t, int start, int len) {
		assertToken(s, t, start, len, AST.LATEST);
	}

	private void assertToken(String s, TOK t, int start, int len, int apiLevel) {
		Lexer lexer = new Lexer(s, true, true, false, true, apiLevel);
		assertEquals(t, lexer.nextToken());
		assertEquals(start, lexer.token.ptr);
		assertEquals(len, lexer.token.len);
		assertEquals(0, lexer.problems.size());
	}
	
	private void assertCharToken(String s, TOK t, int start, int len) {
		Lexer lexer = new Lexer(s, true, true, false, true, AST.LATEST);
		assertEquals(t, lexer.nextToken());
		assertEquals(s.trim(), lexer.token.string);
		assertEquals(start, lexer.token.ptr);
		assertEquals(len, lexer.token.len);
		assertEquals(0, lexer.problems.size());
	}
	
	private void assertStringToken(String s, int start, int len) {
		Lexer lexer = new Lexer(s, true, true, false, true, AST.LATEST);
		assertEquals(TOK.TOKstring, lexer.nextToken());
		assertEquals(s.trim(), lexer.token.string);
		assertEquals(start, lexer.token.ptr);
		assertEquals(len, lexer.token.len);
		assertEquals(0, lexer.problems.size());
	}
	
	private void assertInt32Token(String s, int value, int start, int len) {
		Lexer lexer = new Lexer(s, true, true, false, true, AST.LATEST);
		assertEquals(TOK.TOKint32v, lexer.nextToken());
		//assertEquals(BigInteger.valueOf(value), lexer.token.numberValue);
		assertEquals(s.trim(), lexer.token.string);
		assertEquals(start, lexer.token.ptr);
		assertEquals(len, lexer.token.len);
		assertEquals(0, lexer.problems.size());
	}
	
	private void assertUns32Token(String s, int value, int start, int len) {
		Lexer lexer = new Lexer(s, true, true, false, true, AST.LATEST);
		assertEquals(TOK.TOKuns32v, lexer.nextToken());
		//assertEquals(BigInteger.valueOf(value), lexer.token.numberValue);
		assertEquals(s.trim(), lexer.token.string);
		assertEquals(start, lexer.token.ptr);
		assertEquals(len, lexer.token.len);
		assertEquals(0, lexer.problems.size());
	}
	
	private void assertInt64Token(String s, int value, int start, int len) {
		Lexer lexer = new Lexer(s, true, true, false, true, AST.LATEST);
		assertEquals(TOK.TOKint64v, lexer.nextToken());
		//assertEquals(BigInteger.valueOf(value), lexer.token.numberValue);
		assertEquals(s.trim(), lexer.token.string);
		assertEquals(start, lexer.token.ptr);
		assertEquals(len, lexer.token.len);
		assertEquals(0, lexer.problems.size());
	}
	
	private void assertComment(String string, String comment, int start, int len, TOK tok) {
		Lexer lexer = new Lexer(string, true, true, false, true, AST.LATEST);
		assertEquals(tok, lexer.nextToken());
		assertEquals(start, lexer.token.ptr);
		assertEquals(len, lexer.token.len);
		assertEquals(0, lexer.problems.size());
	}

}
