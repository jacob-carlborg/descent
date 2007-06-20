package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatModifierDeclaration_Test extends AbstractFormatBraceFunction_Test {

	@Override
	protected String getBracePositionOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_MODIFIERS;
	}

	@Override
	protected String getFormattedPrefixForBrace() {
		return "public";
	}

	@Override
	protected String getUnformattedPrefixForBrace() {
		return "public";
	}

	@Override
	protected Map getDefaultOptions() {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_MODIFIERS, DefaultCodeFormatterConstants.END_OF_LINE);
		return options;
	}

}
