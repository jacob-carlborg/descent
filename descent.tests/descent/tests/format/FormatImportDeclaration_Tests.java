package descent.tests.format;


public class FormatImportDeclaration_Tests extends AbstractFormatter_Test {
	
	public void testWithLineEnd() throws Exception {
		assertFormat("import foo.bar;", "import\nfoo.bar;");
	}
	
	public void testWithSpaces() throws Exception {
		assertFormat("import foo.bar;", "import    foo  .   bar   ;");
	}
	
	public void testWithAliasWithSpaces() throws Exception {
		assertFormat("import someAlias = foo.bar;", "import    someAlias = foo  .   bar   ;");
	}
	
	public void testWithSelectiveImportsWithSpaces() throws Exception {
		assertFormat("import foo.bar : something, somethingElse;", "import    foo  .   bar  : something  , somethingElse ;");
	}

}
