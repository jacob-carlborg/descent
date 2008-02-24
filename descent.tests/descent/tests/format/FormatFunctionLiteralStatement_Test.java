package descent.tests.format;

import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatFunctionLiteralStatement_Test extends AbstractFormatBraceInsideFunction_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = super.getDefaultOptions();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_FUNCTION_LITERAL, DefaultCodeFormatterConstants.END_OF_LINE);
		return options;
	}
	
	@Override
	protected boolean needsSemicolon() {
		return true;
	}
	
	// TODO Descent tests return false and fix
	@Override
	protected boolean ignoreWithComments() {
		return true;
	}
	
	@Override
	protected String getBracePositionOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_FUNCTION_LITERAL;
	}
	
	@Override
	protected String getFormattedPrefixForBrace() {
		return "int x = function ()";
	}
	
	@Override
	protected String getUnformattedPrefixForBrace() {
		return "int   x   =   function   (   )";
	}

}
