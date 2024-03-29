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
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_TRY_STATEMENT_ON_SAME_LINE, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_CATCH_STATEMENT_ON_SAME_LINE, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_FINALLY_STATEMENT_ON_SAME_LINE, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CATCH, DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CATCH, DefaultCodeFormatterConstants.FALSE);
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
	
	public void testDontKeepSimpleTryInSameLine() throws Exception {
		assertFormat(
				"try\r\n" +
					"\tint x;\r\n" +
				"catch {\r\n" +
				"} finally {\r\n" +
				"}", 
				
				"try int x;  catch  {   }   finally { }"
			);
	}
	
	public void testKeepSimpleTryInSameLine() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_TRY_STATEMENT_ON_SAME_LINE, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"try int x;\r\n" +
				"catch {\r\n" +
				"} finally {\r\n" +
				"}", 
				
				"try int x;  catch  {   }   finally { }",
				
				options
			);
	}
	
	public void testDontKeepSimpleCatchInSameLine() throws Exception {
		assertFormat(
				"try {\r\n" +
				"} catch(Exception e)\r\n" +
				"\tint x;\r\n" +
				"finally {\r\n" +
				"}", 
				
				"try { }   catch(Exception e)  int x;   finally { }"
			);
	}
	
	public void testDpmtKeepSimpleCatchInSameLine() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_CATCH_STATEMENT_ON_SAME_LINE, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"try {\r\n" +
				"} catch(Exception e) int x;\r\n" +
				"finally {\r\n" +
				"}", 
				
				"try { }   catch(Exception e)  int x;   finally { }",
				
				options
			);
	}
	
	public void testDontKeepSimpleFinallyInSameLine() throws Exception {
		assertFormat(
				"try {\r\n" +
				"} catch {\r\n" +
				"} finally\r\n" +
					"\tint x;", 
				
				"try { }  catch  {   }   finally int x;"
			);
	}
	
	public void testKeepSimpleFinallyInSameLine() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_FINALLY_STATEMENT_ON_SAME_LINE, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"try {\r\n" +
				"} catch {\r\n" +
				"} finally int x;", 
				
				"try { }  catch  {   }   finally int x;",
				
				options
			);
	}
	
	public void testDontINSERT_SPACE_BEFORE_OPENING_PAREN_IN_CATCH() throws Exception {
		assertFormat(
				"try {\r\n" +
				"} catch(Exception e) {\r\n" +
				"} finally\r\n" +
					"\tint x;", 
				
				"try { }  catch  (  Exception e  )  {   }   finally int x;"
			);
	}
	
	public void testINSERT_SPACE_BEFORE_OPENING_PAREN_IN_CATCH() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CATCH, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"try {\r\n" +
				"} catch (Exception e) {\r\n" +
				"} finally\r\n" +
					"\tint x;", 
				
				"try { }  catch  (  Exception e  )  {   }   finally int x;",
				
				options
			);
	}
	
	public void testNotFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CATCH() throws Exception {
		assertFormat(
				"try {\r\n" +
				"} catch(Exception e) {\r\n" +
				"}",
				"try { } catch (Exception e) { }"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CATCH() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CATCH, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"try {\r\n" +
				"} catch( Exception e) {\r\n" +
				"}",
				"try { } catch (Exception e) { }",
				options
				);
	}
	
	public void testBracesAtEndOfLineWithComments() throws Exception {
		assertFormat(
				"try {\r\n" +
				"\tint x;\r\n" +
				"\t/* some comment */\r\n" +
				"} catch {\r\n" +
				"} finally {\r\n" +
				"}", 
				
				"try { int x;\r\n /* some comment */ \r\n   }  catch  {   }   finally { }"
			);
	}
	
	public void testBracesAtEndOfLineWithCommentsNewLineBeforeCatchManyCatches() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_CATCH, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"try {\r\n" +
				"}\r\n" +
				"catch(One e) {\r\n" +
				"}\r\n" +
				"catch(Two e) {\r\n" +
				"} finally {\r\n" +
				"}", 
				
				"try {   }  catch  (One e) {   } catch (Two e)  { }  finally { }",
				
				options
			);
	}

}
