package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.JavaCore;
import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatTypedefDeclaration_Test extends AbstractFormatter_Test {
	
	public void testWithSpaces() throws Exception {
		assertFormat(
				"typedef int x;\r\n",
				
				"typedef   int   x    ;");
	}
	
	public void testWithComments() throws Exception {
		assertFormat(
				"// comment\r\n" +
				"typedef int x;\r\n",
				
				"// comment\r\n" +
				"typedef   int   x    ;");
	}
	
	public void testWithMultiComments() throws Exception {
		assertFormat(
				"/*\r\n" +
				" * comment\r\n" +
				" */" +
				"typedef int x;\r\n",
				
				"/*\r\n" +
				" * comment\r\n" +
				" */" +
				"typedef   int   x    ;");
	}
	
	public void testWithMixedComments() throws Exception {
		assertFormat(
				"typedef /* comment1 */int /* comment2 */x /* comment3 */;\r\n",
				
				"typedef /* comment1 */  int  /* comment2 */ x  /* comment3 */  ;");
	}
	
	public void testInsertSpaceBeforeSemicolon() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON, JavaCore.INSERT);
		assertFormat(
				"typedef int x ;\r\n",
				
				"typedef   int   x    ;",
				
				options);
	}
	
	public void testFragments() throws Exception {
		assertFormat(
				"typedef int x, y, z;\r\n",
				
				"typedef   int   x  ,   y   ,  z  ;");
	}

}
