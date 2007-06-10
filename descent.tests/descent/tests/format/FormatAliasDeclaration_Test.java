package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants2;

public class FormatAliasDeclaration_Test extends AbstractFormatter_Test {
	
	public void testWithSpaces() throws Exception {
		assertFormat(
				"alias int x;\r\n",
				
				"alias   int   x    ;");
	}
	
	public void testWithComments() throws Exception {
		assertFormat(
				"// comment\r\n" +
				"alias int x;\r\n",
				
				"// comment\r\n" +
				"alias   int   x    ;");
	}
	
	public void testWithMultiComments() throws Exception {
		assertFormat(
				"/*\r\n" +
				" * comment\r\n" +
				" */" +
				"alias int x;\r\n",
				
				"/*\r\n" +
				" * comment\r\n" +
				" */" +
				"alias   int   x    ;");
	}
	
	public void testWithMixedComments() throws Exception {
		assertFormat(
				"alias /* comment1 */int /* comment2 */x /* comment3 */;\r\n",
				
				"alias /* comment1 */  int  /* comment2 */ x  /* comment3 */  ;");
	}
	
	public void testInsertSpaceBeforeSemicolon() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants2.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON, DefaultCodeFormatterConstants2.TRUE);
		assertFormat(
				"alias int x ;\r\n",
				
				"alias   int   x    ;",
				
				options);
	}
	
	public void testFragments() throws Exception {
		assertFormat(
				"alias int x, y, z;\r\n",
				
				"alias   int   x  ,   y   ,  z  ;");
	}
	
	public void testFragmentsInsertSpaceBeforeComma() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants2.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS, DefaultCodeFormatterConstants2.TRUE);
		assertFormat(
				"alias int x , y , z;\r\n",
				
				"alias   int   x  ,   y   ,  z  ;",
				
				options);
	}
	
	public void testFragmentsDontInsertSpaceAfterComma() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants2.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS, DefaultCodeFormatterConstants2.FALSE);
		assertFormat(
				"alias int x,y,z;\r\n",
				
				"alias   int   x  ,   y   ,  z  ;",
				
				options);
	}
	
	public void testModifiers() throws Exception {
		assertFormat(
				"public final static alias int x, y, z;\r\n",
				
				"public   final   static    alias   int   x  ,   y   ,  z  ;");
	}

}
