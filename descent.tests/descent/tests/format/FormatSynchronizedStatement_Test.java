package descent.tests.format;

import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatSynchronizedStatement_Test extends AbstractFormatBraceWithSingleInsideFunction_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = super.getDefaultOptions();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SYNCHRONIZED_STATEMENT, DefaultCodeFormatterConstants.END_OF_LINE);
		return options;
	}
	
	@Override
	protected String getBracePositionOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SYNCHRONIZED_STATEMENT;
	}
	
	@Override
	protected String getKeepSimpleStatementInSameLineOption() {
		return DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_SYNCHRONIZED_STATEMENT_ON_SAME_LINE;
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
