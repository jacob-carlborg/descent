package descent.tests.format;

import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatWithStatement_Test extends AbstractFormatBraceInsideFunction_Test {
	
	@Override
	protected void addMoreOptions(Map options) {
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SYNCHRONIZED_STATEMENTS, DefaultCodeFormatterConstants.END_OF_LINE);
	}
	
	@Override
	protected String getBracePositionOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SYNCHRONIZED_STATEMENTS;
	}
	
	@Override
	protected String getFormattedPrefixForBrace() {
		return "synchronized(lock)";
	}
	
	@Override
	protected String getUnformattedPrefixForBrace() {
		return "synchronized ( lock ) ";
	}

}
