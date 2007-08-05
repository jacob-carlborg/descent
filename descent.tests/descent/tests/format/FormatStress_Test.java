package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatStress_Test extends AbstractFormatter_Test {

	@Override
	protected Map getDefaultOptions() {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_CASE, DefaultCodeFormatterConstants.TRUE);
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

}
