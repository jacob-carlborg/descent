package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import descent.core.ToolFactory;
import descent.core.formatter.CodeFormatter;

/**
 * An abstract class for formatter tests.
 */
public abstract class AbstractFormatter_Test extends TestCase {
	
	protected final static Map EMPTY_MAP = new HashMap();
	
	/**
	 * Formats "original" with the default options (see {@link #getDefaultOptions()})
	 * and compares the resulting string with the expected string.
	 */
	protected final void assertFormat(String expected, String original) throws Exception {
		assertFormat(expected, original, EMPTY_MAP);
	}
	
	/**
	 * Formats "original" with the default options (see {@link #getDefaultOptions()})
	 * merged with the "overrideOptions" given (which have more precedence),
	 * and compares the resulting string with the expected string.
	 */
	protected void assertFormat(String expected, String original, Map overrideOptions) throws Exception {
		Document document = new Document(original);
		
		Map mergedOptions = getDefaultOptions();
		mergedOptions.putAll(overrideOptions);
		
		CodeFormatter formatter =  ToolFactory.createCodeFormatter(mergedOptions);
		TextEdit edit = formatter.format(CodeFormatter.K_COMPILATION_UNIT, original, 0, original.length(), 0, null);
		edit.apply(document);
		
		String string = document.get();
		assertEquals(expected, string);
	}
	
	/**
	 * Must return the default options used in the formatting test.
	 * This is to not rely on the default values of DefaultCodeFormatterOptions,
	 * which may change, thus braking some tests.
	 */
	protected abstract Map getDefaultOptions();

}
