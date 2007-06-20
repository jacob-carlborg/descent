package descent.tests.format;

import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatForeachStatement_Test extends AbstractFormatBraceWithSingleInsideFunction_Test {
	
	@Override
	protected void addMoreOptions(Map options) {
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_LOOP_STATEMENT, DefaultCodeFormatterConstants.END_OF_LINE);
	}
	
	@Override
	protected String getBracePositionOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_LOOP_STATEMENT;
	}
	
	@Override
	protected String getFormattedPrefixForBrace() {
		return "foreach(element; collection)";
	}
	
	@Override
	protected String getUnformattedPrefixForBrace() {
		return "foreach   (    element   ;    collection   )";
	}

}
