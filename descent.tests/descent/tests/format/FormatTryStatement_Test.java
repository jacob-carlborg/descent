package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatTryStatement_Test extends AbstractFormatInsideFunction_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = super.getDefaultOptions();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TRY_CATCH_FINALLY, DefaultCodeFormatterConstants.END_OF_LINE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_CATCH, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_FINALLY, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_FINALLY, DefaultCodeFormatterConstants.FALSE);
		return options;
	}
	
	public void testBracesAtEndOfLine() throws Exception {
		assertFormat(
				"try {\r\n" +
				"} catch {\r\n" +
				"} finally {\r\n" +
				"}", 
				
				"try {    }  catch  {   }   finally { }"
			);
	}
	
	public void testCatchBracesNextLine() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TRY_CATCH_FINALLY, DefaultCodeFormatterConstants.NEXT_LINE);
		assertFormat(
				"try\r\n" +
				"{\r\n" +
				"} catch\r\n" +
				"{\r\n" +
				"} finally\r\n" +
				"{\r\n" +
				"}", 
				
				"try {    }  catch  {   }   finally { }",
				
				options
			);
	}
	
	public void testCatchBracesNextLineShifted() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TRY_CATCH_FINALLY, DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		assertFormat(
				"try\r\n" +
					"\t{\r\n" +
					"\t} catch\r\n" +
					"\t{\r\n" +
					"\t} finally\r\n" +
					"\t{\r\n" +
					"\t}", 
				
					"try {    }  catch  {   }   finally { }",
				
				options
			);
	}
	
	public void testBracesNextLineShiftedWithMembers() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TRY_CATCH_FINALLY, DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		assertFormat(
				"try\r\n" +
					"\t{\r\n" +
					"\t\tint x;\r\n" +
					"\t} catch\r\n" +
					"\t{\r\n" +
					"\t\tfloat x;\r\n" +
					"\t} finally\r\n" +
					"\t{\r\n" +
					"\t\tireal z;\r\n" +
					"\t}", 
				
				"try {  int x;  }   catch   {  float x;  }  finally {  ireal z; }",
				
				options
			);
	}
	
	public void testInsertNewLineBeforeCatch() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_CATCH, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"try {\r\n" +
				"}\r\n" +
				"catch {\r\n" +
				"} finally {\r\n" +
				"}", 
				
				"try {    }  catch  {   }   finally   {   }",
				
				options
			);
	}
	
	public void testInsertNewLineBeforeFinally() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_FINALLY, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"try {\r\n" +
				"} catch {\r\n" +
				"}\r\n" +
				"finally {\r\n" +
				"}", 
				
				"try {    }  catch  {   }   finally   {   }",
				
				options
			);
	}
	
	public void testCatchWithComments() throws Exception {
		assertFormat(
				"/*\r\n" +
				" * Some\r\n" +
				" * comment\r\n" +
				" */\r\n" +
				"try { // comment\r\n" +
				"} catch { // comment\r\n" +
				"} // trailing", 
				
				"/*\r\n" +
				" * Some\r\n" +
				" * comment\r\n" +
				" */\r\n" +
				"try  { // comment\r\n   }  catch {   // comment\r\n   } // trailing"
			);
	}

}
