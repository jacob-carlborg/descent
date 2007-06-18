package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatModuleDeclaration_Tests extends AbstractFormatter_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_MODULE, "0");
		options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AFTER_MODULE, "0");
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON, DefaultCodeFormatterConstants.FALSE);
		return options;
	}
	
	public void testWithLineEnd() throws Exception {
		assertFormat(
				"module foo.bar;\r\n", 
				
				"module\n" +
				"foo.bar;"
			);
	}
	
	public void testWithSpaces() throws Exception {
		assertFormat(
				"module foo.bar;\r\n", 
				
				"module    foo  .   bar   ;"
			);
	}
	
	public void testWithPreCommentSingle() throws Exception {
		assertFormat(
				"// comment\r\n" +
				"module foo.bar;\r\n", 
				
				"// comment\r\n" +
				"module foo.bar;"
			);
	}
	
	public void testWithPreCommentMulti() throws Exception {
		assertFormat(
				"/* comment\r\n" +
				" * hola\r\n" +
				" */\r\n" +
				"module foo.bar;\r\n", 
				
				"/* comment\r\n" +
				" * hola\r\n" +
				" */\r\n" +
				"module foo.bar;"
			);
	}
	
	public void testWithPreCommentMulti2() throws Exception {
		assertFormat(
				"/** comment\r\n" +
				" * hola\r\n" +
				" */\r\n" +
				"module foo.bar;\r\n", 
				
				"/** comment\r\n" +
				" * hola\r\n" +
				" */\r\n" +
				"module foo.bar;"
			);
	}
	
	public void testWithTrailingComment() throws Exception {
		assertFormat(
				"module foo.bar; // comment", 
				
				"module foo.bar;           // comment"
			);
	}
	
	public void testWithCommentBetweenModuleAndName() throws Exception {
		assertFormat(
				"module /* comment */foo.bar;\r\n", 
				
				"module    /* comment */         foo.bar;"
			);
	}
	
	public void testLinesBefore() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_MODULE, "3");
		assertFormat(
				"\r\n\r\n\r\nmodule foo.bar;\r\n", 
				
				"module foo.bar;",
				
				options);
	}
	
	public void testLinesAfter() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AFTER_MODULE, "3");
		assertFormat(
				"module foo.bar;\r\n\r\n\r\n\r\n", 
				
				"module foo.bar;",
				
				options);
	}
	
	public void testSpaceBeforeSemicolon() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"module foo.bar ;\r\n", 
				
				"module foo.bar;",
				
				options);
	}

}
