package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;


public class FormatImportDeclaration_Tests extends AbstractFormatter_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON, DefaultCodeFormatterConstants.FALSE);
		return options;
	}
	
	public void testWithLineEnd() throws Exception {
		assertFormat(
				"import foo.bar;\r\n",
				
				"import\nfoo.bar;"
			);
	}
	
	public void testWithSpaces() throws Exception {
		assertFormat(
				"import foo.bar;\r\n", 
				
				"import    foo  .   bar   ;"
			);
	}
	
	public void testWithAliasWithSpaces() throws Exception {
		assertFormat(
				"import someAlias = foo.bar;\r\n", 
				
				"import    someAlias = foo  .   bar   ;"
			);
	}
	
	public void testWithSelectiveImportsWithSpaces() throws Exception {
		assertFormat(
				"import foo.bar : something, somethingElse;\r\n", 
				
				"import    foo  .   bar  : something  , somethingElse ;"
			);
	}
	
	public void testWithSelectiveImportsAndAliasesWithSpaces() throws Exception {
		assertFormat(
				"import foo.bar : anAlias = something, otherAlias = somethingElse;\r\n", 
				
				"import    foo  .   bar  :   anAlias  =   something  ,   otherAlias   =   somethingElse ;"
			);
	}
	
	public void testWithPreCommentSingle() throws Exception {
		assertFormat(
				"// comment\r\n" +
				"import foo.bar;\r\n",
				
				"// comment\r\n" +
				"import\nfoo.bar;"
			);
	}
	
	public void testWithPreCommentMulti() throws Exception {
		assertFormat(
				"/* comment\r\n" +
				" * hola\r\n" +
				" */\r\n" +
				"import foo.bar;\r\n",
				
				"/* comment\r\n" +
				" * hola\r\n" +
				" */\r\n" +
				"import\nfoo.bar;"
			);
	}
	
	public void testWithPreCommentMulti2() throws Exception {
		assertFormat(
				"/** comment\r\n" +
				" * hola\r\n" +
				" */\r\n" +
				"import foo.bar;\r\n",
				
				"/** comment\r\n" +
				" * hola\r\n" +
				" */\r\n" +
				"import\nfoo.bar;"
			);
	}
	
	public void testWithTrailingComment() throws Exception {
		assertFormat(
				"import foo.bar; // comment",
				
				"import foo.bar;          // comment"
			);
	}
	
	public void testWithStatic() throws Exception {
		assertFormat(
				"static import foo.bar;\r\n",
				
				"static  \r\n  import foo.bar;"
			);
	}
	
	public void testWithModifiers() throws Exception {
		assertFormat(
				"private public import foo.bar;\r\n",
				
				"private \r\n   public  \r\n  import foo.bar;"
			);
	}
	
	public void testWithModifiersAndStatic() throws Exception {
		assertFormat(
				"private public static import foo.bar;\r\n",
				
				"private \r\n   public  \r\n  static    \r\n   import foo.bar;"
			);
	}
	
	public void testWithStaticAndModifiers() throws Exception {
		assertFormat(
				"static private public import foo.bar;\r\n",
				
				"static   \r\n    private \r\n   public  \r\n  import foo.bar;"
			);
	}
	
	public void testSpaceBeforeSemicolon() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"import foo.bar ;\r\n",
				
				"import foo.bar;",
				
				options
			);
	}
	
	public void testManyImports() throws Exception {
		assertFormat(
				"import one;\r\n" +
				"import two;\r\n" +
				"import three;\r\n",
				
				"import one; import two;    import three;"
			);
	}

}
