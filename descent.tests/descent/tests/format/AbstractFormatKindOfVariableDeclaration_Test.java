package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public abstract class AbstractFormatKindOfVariableDeclaration_Test extends AbstractFormatter_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS, DefaultCodeFormatterConstants.TRUE);
		return options;
	}
	
	protected abstract String getPrefixOfKindOfVariableDeclaration();
	
	public void testWithSpaces() throws Exception {
		assertFormat(
				getPrefixOfKindOfVariableDeclaration() + "int x;",
				
				getPrefixOfKindOfVariableDeclaration() + "int   x    ;");
	}
	
	public void testWithComments() throws Exception {
		assertFormat(
				"// comment\r\n" +
				getPrefixOfKindOfVariableDeclaration() + "int x;",
				
				"// comment\r\n" +
				getPrefixOfKindOfVariableDeclaration() + "int   x    ;");
	}
	
	public void testWithMultiComments() throws Exception {
		assertFormat(
				"/*\r\n" +
				" * comment\r\n" +
				" */" +
				getPrefixOfKindOfVariableDeclaration() + "int x;",
				
				"/*\r\n" +
				" * comment\r\n" +
				" */" +
				getPrefixOfKindOfVariableDeclaration() + "int   x    ;");
	}
	
	public void testWithMixedComments() throws Exception {
		assertFormat(
				getPrefixOfKindOfVariableDeclaration() + "/* comment1 */int /* comment2 */x /* comment3 */;",
				
				getPrefixOfKindOfVariableDeclaration() + "/* comment1 */  int  /* comment2 */ x  /* comment3 */  ;");
	}
	
	public void testInsertSpaceBeforeSemicolon() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				getPrefixOfKindOfVariableDeclaration() + "int x ;",
				
				getPrefixOfKindOfVariableDeclaration() + "int   x    ;",
				
				options);
	}
	
	public void testFragments() throws Exception {
		assertFormat(
				getPrefixOfKindOfVariableDeclaration() + "int x, y, z;",
				
				getPrefixOfKindOfVariableDeclaration() + "int   x  ,   y   ,  z  ;");
	}
	
	public void testFragmentsInsertSpaceBeforeComma() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				getPrefixOfKindOfVariableDeclaration() + "int x , y , z;",
				
				getPrefixOfKindOfVariableDeclaration() + "int   x  ,   y   ,  z  ;",
				
				options);
	}
	
	public void testFragmentsDontInsertSpaceAfterComma() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS, DefaultCodeFormatterConstants.FALSE);
		assertFormat(
				getPrefixOfKindOfVariableDeclaration() + "int x,y,z;",
				
				getPrefixOfKindOfVariableDeclaration() + "int   x  ,   y   ,  z  ;",
				
				options);
	}

}
