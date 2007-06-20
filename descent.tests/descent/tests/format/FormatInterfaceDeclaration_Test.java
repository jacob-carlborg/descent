package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class FormatInterfaceDeclaration_Test extends AbstractFormatBraceFunction_Test {

	@Override
	protected String getBracePositionOptionName() {
		return DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION;
	}

	@Override
	protected String getFormattedPrefixForBrace() {
		return "interface Class";
	}

	@Override
	protected String getUnformattedPrefixForBrace() {
		return "interface    Class";
	}

	@Override
	protected Map getDefaultOptions() {
		Map options = new HashMap();
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION, DefaultCodeFormatterConstants.END_OF_LINE);
		return options;
	}

}
