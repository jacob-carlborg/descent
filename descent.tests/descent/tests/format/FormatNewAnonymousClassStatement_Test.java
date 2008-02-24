package descent.tests.format;

import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatNewAnonymousClassStatement_Test extends AbstractFormatBraceInsideFunction_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = super.getDefaultOptions();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ANONYMOUS_TYPE, DefaultCodeFormatterConstants.END_OF_LINE);
		return options;
	}
	
	// TODO Descent tests return false and fix
	@Override
	protected boolean ignoreWithComments() {
		return true;
	}
	
	@Override
	protected boolean needsSemicolon() {
		return true;
	}
	
	@Override
	protected String getBracePositionOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ANONYMOUS_TYPE;
	}
	
	@Override
	protected String getFormattedPrefixForBrace() {
		return "int x = new class()";
	}
	
	@Override
	protected String getUnformattedPrefixForBrace() {
		return "int   x   =  new  class   (   )";
	}

}
