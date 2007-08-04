package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatTemplateDeclaration_Test extends AbstractFormatBrace_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = super.getDefaultOptions();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TEMPLATE_DECLARATIONS, DefaultCodeFormatterConstants.FALSE);
		return options;
	}
	
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
		return "template Class()";
	}

	@Override
	protected String getUnformattedPrefixForBrace() {
		return "template    Class   (   )";
	}
	
	public void testNotFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TEMPLATE_DECLARATIONS() throws Exception {
		assertFormat(
				"template Class() {\r\n" +
				"}",
				"template Class ( ) { }"
				);
	}
	
	public void testFORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TEMPLATE_DECLARATIONS() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TEMPLATE_DECLARATIONS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"template Class () {\r\n" +
				"}",
				"template Class ( ) { }",
				options
				);
	}

}
