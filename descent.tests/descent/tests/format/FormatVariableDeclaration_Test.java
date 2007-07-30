package descent.tests.format;


public class FormatVariableDeclaration_Test extends AbstractFormatKindOfVariableDeclaration_Test {

	@Override
	protected String getPrefixOfKindOfVariableDeclaration() {
		return "";
	}
	
	public void testManyVariableDeclarations() throws Exception {
		assertFormat(
				"class X {\r\n" +
				"\tint x;\r\n" +
				"\tint y;\r\n" +
				"\tint z;\r\n" +
				"}"
				, 
				
				"class  X  {  int   x;  int   y;  int   z;  }"
				);
	}
	

}
