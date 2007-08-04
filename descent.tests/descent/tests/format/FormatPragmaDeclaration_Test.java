package descent.tests.format;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatPragmaDeclaration_Test extends AbstractFormatBrace_Test {
	
	@Override
	protected String getBracePositionOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_PRAGMAS;
	}
	
	@Override
	protected String getIndentCompareToParentOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_PRAGMA_HEADER;
	}

	@Override
	protected String getFormattedPrefixForBrace() {
		return "pragma(msg, \"hello\")";
	}

	@Override
	protected String getUnformattedPrefixForBrace() {
		return "pragma ( msg , \"hello\"  )";
	}	

}
