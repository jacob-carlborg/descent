package descent.tests.format;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatTemplateDeclaration_Test extends AbstractFormatBrace_Test {
	
	@Override
	protected String getBracePositionOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TEMPLATE_DECLARATION;
	}
	
	@Override
	protected String getIndentCompareToParentOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_TEMPLATE_HEADER;
	}

	@Override
	protected String getFormattedPrefixForBrace() {
		return "template Class ()";
	}

	@Override
	protected String getUnformattedPrefixForBrace() {
		return "template    Class   (   )";
	}	

}
