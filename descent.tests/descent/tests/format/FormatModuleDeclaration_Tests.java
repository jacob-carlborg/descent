package descent.tests.format;


public class FormatModuleDeclaration_Tests extends AbstractFormatter_Test {
	
	public void testWithLineEnd() throws Exception {
		assertFormat("module foo.bar;", "module\nfoo.bar;");
	}
	
	public void testWithSpaces() throws Exception {
		assertFormat("module foo.bar;", "module    foo  .   bar   ;");
	}

}
