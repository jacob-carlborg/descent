package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatClassDeclaration_Test extends AbstractFormatBrace_Test {

	@Override
	protected String getBracePositionOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION;
	}
	
	@Override
	protected String getIndentCompareToParentOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_TYPE_HEADER;
	}

	@Override
	protected String getFormattedPrefixForBrace() {
		return "class Class";
	}

	@Override
	protected String getUnformattedPrefixForBrace() {
		return "class    Class";
	}

	@Override
	protected Map getDefaultOptions() {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION, DefaultCodeFormatterConstants.END_OF_LINE);
		return options;
	}

}
