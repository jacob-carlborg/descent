package descent.tests.format;

public class FormatEnumDeclaration_Test extends AbstractFormatter_Test {
	
	public void testEmptyAnonymous() throws Exception {
		assertFormat(
				"enum {\r\n" +
				"}\r\n", 
				
				"enum   {    }"
			);
	}

}
