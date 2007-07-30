package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatAlignDeclaration_Test extends AbstractFormatBrace_Test {
	
	@Override
	protected Map getDefaultOptions() {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ALIGN_DECLARATION, DefaultCodeFormatterConstants.END_OF_LINE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ALIGN_DECLARATIONS, DefaultCodeFormatterConstants.FALSE);
		return options;
	}

	@Override
	protected String getBracePositionOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ALIGN_DECLARATION;
	}
	
	@Override
	protected String getIndentCompareToParentOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_ALIGN_HEADER;
	}

	@Override
	protected String getFormattedPrefixForBrace() {
		return "align(4)";
	}

	@Override
	protected String getUnformattedPrefixForBrace() {
		return "align   (   4   )";
	}
	
	public void testDontINSERT_SPACE_BEFORE_OPENING_PAREN() throws Exception {
		assertFormat(
				"align(4) {\r\n" +
				"}",
				
				"align   (   4    ) { }"
				);
	}
	
	public void testINSERT_SPACE_BEFORE_OPENING_PAREN() throws Exception {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ALIGN_DECLARATIONS, DefaultCodeFormatterConstants.TRUE);
		assertFormat(
				"align (4) {\r\n" +
				"}",
				
				"align   (   4    ) { }",
				
				options
				);
	}


}
