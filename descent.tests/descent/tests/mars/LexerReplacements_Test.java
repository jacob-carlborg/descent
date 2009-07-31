package descent.tests.mars;


public class LexerReplacements_Test extends Parser_Test {
	
	public void test__FILE__ConcatenatedWithStrings() throws Exception {
		String s = "pragma(msg, \">>\" __FILE__ \":\");";
		assertEquals(s.substring(0, s.length() - 1), getDeclarationsNoProblems(s).get(0).toString());
	}

}
