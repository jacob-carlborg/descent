package descent.tests.format;


public class FormatModuleDeclaration_Tests extends AbstractFormatter_Test {
	
	public void testWithLineEnd() throws Exception {
		assertFormat("module\nfoo.bar;", "module foo.bar;");
	}
	
	public void testWithSpaces() throws Exception {
		assertFormat("module    foo  .   bar   ;", "module foo.bar;");
	}

}
