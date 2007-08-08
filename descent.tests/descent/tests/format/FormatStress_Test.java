package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatStress_Test extends AbstractFormatter_Test {

	@Override
	protected Map getDefaultOptions() {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_CASE, DefaultCodeFormatterConstants.TRUE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_BREAK_COMPARE_TO_SWITCH, DefaultCodeFormatterConstants.TRUE);
		return options;
	}
	
	public void testExternWithVariable() throws Exception {
		assertFormat(
				"extern(C) {\r\n" + 
				"\tint x;\r\n" + 
				"}",
				"extern (C) { int x; }"
				);
	}
	
	public void testExternWithModifier() throws Exception {
		assertFormat(
				"private extern(C) {\r\n" + 
				"}",
				"private extern (C) { }"
				);
	}
	
	public void testStaticConstructorWithModifier() throws Exception {
		assertFormat(
				"private static this() {\r\n" + 
				"}",
				"private static this() { }"
				);
	}
	
	public void testInvariantWithModifier() throws Exception {
		assertFormat(
				"private invariant() {\r\n" + 
				"}",
				"private invariant() { }"
				);
	}
	
	public void testUnittestWithFunctionWithModifier() throws Exception {
		assertFormat(
				"unittest {\r\n" + 
				"\tstatic void foo() {\r\n" + 
				"\t}\r\n" + 
				"}",
				"unittest { static void foo() { } }"
				);
	}
	
	public void testStaticConstructor() throws Exception {
		assertFormat(
				"static this() {\r\n" +
				"}",
				"static this() {}"
				);
	}
	
	public void testStaticDestructor() throws Exception {
		assertFormat(
				"static ~this() {\r\n" +
				"}",
				"static ~this() {}"
				);
	}
	
	public void testAutoInIf() throws Exception {
		assertFormat(
				"unittest {\r\n" + 
				"\tif(auto m = x) {\r\n" + 
				"\t}\r\n" + 
				"}", 
				"unittest { if (auto m = x) { } }"
				);
	}
	
	public void testSwitchCase() throws Exception {
		assertFormat(
				"unittest {\r\n" + 
				"\tswitch(x) {\r\n" +
				"\t\tcase 1:\r\n" +
				"\t\t\tbreak;\r\n" +
				"\t}\r\n" + 
				"}", 
				"unittest { switch(x) { case 1: break; } }"
				);
	}
	
	public void testSwitchCaseWithManyExpressions() throws Exception {
		assertFormat(
				"unittest {\r\n" + 
				"\tswitch(x) {\r\n" +
				"\t\tcase 1, 2, 3:\r\n" +
				"\t\t\tbreak;\r\n" +
				"\t}\r\n" + 
				"}", 
				"unittest { switch(x) { case 1, 2, 3: break; } }"
				);
	}
	
	public void testStaticConstWithManyInitializers() throws Exception {
		assertFormat(
				"static const RequestMethod Get = {\"GET\"}, Put = {\"PUT\"};", 
				"static const RequestMethod Get = {\"GET\"}, Put = {\"PUT\"};");
	}
	
	public void testTwoStatements() throws Exception {
		assertFormat(
				"void foo() {\r\n" +
				"\tint x;\r\n" +
				"\r\n" +
				"\tint x;\r\n" +
				"}",
				
				"void foo() {\r\n" +
				"\tint x;\r\n" +
				"\r\n" +
				"\tint x;\r\n" +
				"}");
	}
	
	public void testTwoStatementsWithComments() throws Exception {
		assertFormat(
				"void foo() {\r\n" +
				"\tint x;\r\n" +
				"\r\n" +
				"\t// Comment\r\n" +
				"\tint x;\r\n" +
				"}",
				
				"void foo() {\r\n" +
				"\tint x;\r\n" +
				"\r\n" +
				"\t// Comment\r\n" +
				"\tint x;\r\n" +
				"}");
	}
	
	public void testTwoDeclarationsWithComments() throws Exception {
		assertFormat(
				"int x;\r\n" +
				"\r\n" +
				"// Comment\r\n" +
				"alias int x;",
				
				"int x;\r\n" +
				"\r\n" +
				"// Comment\r\n" +
				"alias int x;");
	}
	
	public void testTwoStatementsOneLineBetween() throws Exception {
		assertFormat(
				"void foo() {\r\n" +
				"\tint x;\r\n" +
				"\tint x;\r\n" +
				"}",
				
				"void foo() {\r\n" +
				"\tint x;\r\n" +
				"\tint x;\r\n" +
				"}");
	}

}
