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
	public static final String NEXT_LINE_ON_WRAP ="next_line_on_wrap";
	public static final String NEXT_LINE_SHIFTED = "next_line_shifted";
	
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
}
