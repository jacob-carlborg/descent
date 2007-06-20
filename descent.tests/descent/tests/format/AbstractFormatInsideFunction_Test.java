package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

/**
 * Abstract formatter test for statements. It wraps each actual and expected
 * string with a function, so you can just concentrate on the formatting
 * of the statement inside the function.
 */
public abstract class AbstractFormatInsideFunction_Test extends AbstractFormatter_Test {

	/**
	 * Returns default options for function formatting configuration.
	 */
	@Override
	protected Map getDefaultOptions() {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_FUNCTION_DECLARATION, DefaultCodeFormatterConstants.END_OF_LINE);
		return options;
	}
	
	@Override
	protected void assertFormat(String expected, String original, Map options) throws Exception {
		super.assertFormat(putInsideFunction(expected), putInsideFunction(original), options);
	}
	
	private static String putInsideFunction(String string) {
		StringBuilder sb = new StringBuilder();
		sb.append("void foo() {\r\n");
		sb.append(indent(string));
		sb.append("}");
		return sb.toString();
	}
	
	private static String indent(String string) {
		String[] lines = string.split("\r\n");
		
		StringBuilder sb = new StringBuilder();
		for(String line : lines) {
			sb.append("\t");
			sb.append(line);
			sb.append("\r\n");
		}
		return sb.toString();
	}

}
