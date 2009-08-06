package descent.core.formatter;

import java.util.Map;

import descent.core.JavaCore;
import descent.internal.formatter.DefaultCodeFormatterOptions;
import descent.internal.formatter.Alignment;

/**
 * Constants used to set up the options of the code formatter.
 * <p>
 * This class is not intended to be instantiated or subclassed by clients.
 * </p>
 * 
 * @since 3.0
 */
public class DefaultCodeFormatterConstants {
	
	/**
	 * <pre>
	 * FORMATTER / The wrapping is done by indenting by one compare to the current indentation.
	 * </pre>
	 * @since 3.0
	 */
	public static final int INDENT_BY_ONE= 2;
	
	/**
	 * <pre>
	 * FORMATTER / The wrapping is done by using the current indentation.
	 * </pre>
	 * @since 3.0
	 */
	public static final int INDENT_DEFAULT= 0;
	/**
	 * <pre>
	 * FORMATTER / The wrapping is done by indenting on column under the splitting location.
	 * </pre>
	 * @since 3.0
	 */
	public static final int INDENT_ON_COLUMN = 1;
	
	/*
	 * Private constants. Not in javadoc
	 */
	private static final IllegalArgumentException WRONG_ARGUMENT = new IllegalArgumentException();
	
	public static final String PROFILE_DESCENT_DEFAULTS = "descent.ui.formatter.defaults.descent_defaults";
	public static final String PROFILE_JAVA_DEFAULTS = "descent.ui.formatter.defaults.java_defaults";
	public static final String PROFILE_C_SHARP_DEFAULTS = "decent.ui.formatter.defaults.c_sharp_defaults";
	public static final String DEFAULT_PROFILE = PROFILE_DESCENT_DEFAULTS;
	
	// Boolean mappings
	public static final String FALSE = "false";
	public static final String TRUE = "true";
	
	// Brace positions
	public static final String END_OF_LINE = "end_of_line";
	public static final String NEXT_LINE = "next_line";
	public static final String NEXT_LINE_SHIFTED = "next_line_shifted";
	
	// Indentation style
	public static final String NO_INDENTATION = "no_indentation";
	public static final String INDENT_NORMAL = "indent_normal";
	public static final String INDENT_HEADING_BACK = "indent_heading_back";
	
	// Indentation type
	public static final String MIXED = "mixed";
	public static final String SPACE = JavaCore.SPACE;
	public static final String TAB = JavaCore.TAB;
	
	// Alignment options
	/* TODO redo alignment options to JDT-style in a later release, these are 
	        just the most obvious/simple ones. */ 
	
	// Don't wrap at all (leave long lines intact)
	public static final int DO_NOT_WRAP = 
		Alignment.M_NO_ALIGNMENT;
	
	// Wrap by the continuation indentation if there's a long line
	public static final int WRAP_ONLY_WHEN_NECESSARY = 
		Alignment.M_COMPACT_SPLIT;
	
	// Wrap to the column if there's a long line
	public static final int WRAP_ON_COLUMN =
		Alignment.M_INDENT_ON_COLUMN | Alignment.M_COMPACT_SPLIT;
	
	// Place one fragment per line
	public static final int WRAP_ONE_FRAGMENT_PER_LINE = 
		Alignment.M_FORCE | Alignment.M_INDENT_ON_COLUMN | 
		Alignment.M_NEXT_PER_LINE_SPLIT;
	
	// Formatter value names
	/* EVAL-FOR-EACH
	 * 
	 * print DST "\tpublic static final String $$_{'constName'} = JavaCore.PLUGIN_ID + \".formatter.$$_{'optName'}\";\n";
	 * 
	 */

	public static Map getBuiltInProfile(String name) {
		return DefaultCodeFormatterOptions.getBuiltInProfile(name).getMap();
	}
	
	public static Map getDefaultSettings() {
		return DefaultCodeFormatterOptions.getBuiltInProfile(DEFAULT_PROFILE).getMap();
	}
	
	/**
	 * <p>Return the indentation style of the given alignment value.
	 * The given alignment value should be created using the <code>createAlignmentValue(boolean, int, int)</code>
	 * API.
	 * </p>
	 *
	 * @param value the given alignment value
	 * @return the indentation style of the given alignment value
	 * @see #createAlignmentValue(boolean, int, int)
	 * @exception IllegalArgumentException if the given alignment value is null, or if it 
	 * doesn't have a valid format.
	 */
	public static int getIndentStyle(String value) {
		if (value == null) {
			throw WRONG_ARGUMENT;
		}
		try {
			int existingValue = Integer.parseInt(value);
			if ((existingValue & Alignment.M_INDENT_BY_ONE) != 0) {
				return INDENT_BY_ONE;
			} else if ((existingValue & Alignment.M_INDENT_ON_COLUMN) != 0) {
				return INDENT_ON_COLUMN;
			} else {
				return INDENT_DEFAULT;
			}
		} catch (NumberFormatException e) {
			throw WRONG_ARGUMENT;
		}
	}
}
