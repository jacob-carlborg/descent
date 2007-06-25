package descent.core.formatter;

import java.util.Map;

import descent.core.JavaCore;
import descent.internal.formatter.DefaultCodeFormatterOptions;
import descent.internal.formatter.align.Alignment2;

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
	
	// Indentation options
	public static final int INDENT_DEFAULT= 0;
	public static final int INDENT_ON_COLUMN = 1;
	public static final int INDENT_BY_ONE= 2;
	
	// Wrapping/splitting for long lines
	public static final int WRAP_COMPACT= 1;
	public static final int WRAP_COMPACT_FIRST_BREAK= 2;
	public static final int WRAP_NEXT_PER_LINE= 5;
	public static final int WRAP_NEXT_SHIFTED= 4;
	public static final int WRAP_NO_SPLIT= 0;
	public static final int WRAP_ONE_PER_LINE= 3;
	
	// Formatter value names
	/* EVAL-FOR-EACH
	 * 
	 * print DST "\tpublic static final String $$_{'constName'} = JavaCore.PLUGIN_ID + \".formatter.$$_{'optName'}\";\n";
	 * 
	 */
	
	private static final IllegalArgumentException WRONG_ARGUMENT = new IllegalArgumentException("Wrong argument!");
	
	public static String createAlignmentValue(boolean forceSplit, int wrapStyle, int indentStyle) {
		int alignmentValue = 0; 
		switch(wrapStyle) {
			case WRAP_COMPACT :
				alignmentValue |= Alignment2.M_COMPACT_SPLIT;
				break;
			case WRAP_COMPACT_FIRST_BREAK :
				alignmentValue |= Alignment2.M_COMPACT_FIRST_BREAK_SPLIT;
				break;
			case WRAP_NEXT_PER_LINE :
				alignmentValue |= Alignment2.M_NEXT_PER_LINE_SPLIT;
				break;
			case WRAP_NEXT_SHIFTED :
				alignmentValue |= Alignment2.M_NEXT_SHIFTED_SPLIT;
				break;
			case WRAP_ONE_PER_LINE :
				alignmentValue |= Alignment2.M_ONE_PER_LINE_SPLIT;
				break;
		}		
		if (forceSplit) {
			alignmentValue |= Alignment2.M_FORCE;
		}
		switch(indentStyle) {
			case INDENT_BY_ONE :
				alignmentValue |= Alignment2.M_INDENT_BY_ONE;
				break;
			case INDENT_ON_COLUMN :
				alignmentValue |= Alignment2.M_INDENT_ON_COLUMN;
		}
		return String.valueOf(alignmentValue);
	}

	public static Map getBuiltInProfile(String name) {
		return DefaultCodeFormatterOptions.getBuiltInProfile(name).getMap();
	}
	
	public static Map getDefaultSettings() {
		return DefaultCodeFormatterOptions.getBuiltInProfile(DEFAULT_PROFILE).getMap();
	}

	public static boolean getForceWrapping(String value) {
		if (value == null) {
			throw WRONG_ARGUMENT;
		}
		try {
			int existingValue = Integer.parseInt(value);
			return (existingValue & Alignment2.M_FORCE) != 0;
		} catch (NumberFormatException e) {
			throw WRONG_ARGUMENT;
		}
	}
	
	public static int getIndentStyle(String value) {
		if (value == null) {
			throw WRONG_ARGUMENT;
		}
		try {
			int existingValue = Integer.parseInt(value);
			if ((existingValue & Alignment2.M_INDENT_BY_ONE) != 0) {
				return INDENT_BY_ONE;
			} else if ((existingValue & Alignment2.M_INDENT_ON_COLUMN) != 0) {
				return INDENT_ON_COLUMN;
			} else {
				return INDENT_DEFAULT;
			}
		} catch (NumberFormatException e) {
			throw WRONG_ARGUMENT;
		}
	}

	public static int getWrappingStyle(String value) {
		if (value == null) {
			throw WRONG_ARGUMENT;
		}
		try {
			int existingValue = Integer.parseInt(value) & Alignment2.SPLIT_MASK;
			switch(existingValue) {
				case Alignment2.M_COMPACT_SPLIT :
					return WRAP_COMPACT;
				case Alignment2.M_COMPACT_FIRST_BREAK_SPLIT :
					return WRAP_COMPACT_FIRST_BREAK;
				case Alignment2.M_NEXT_PER_LINE_SPLIT :
					return WRAP_NEXT_PER_LINE;
				case Alignment2.M_NEXT_SHIFTED_SPLIT :
					return WRAP_NEXT_SHIFTED;
				case Alignment2.M_ONE_PER_LINE_SPLIT :
					return WRAP_ONE_PER_LINE;
				default:
					return WRAP_NO_SPLIT;
			}
		} catch (NumberFormatException e) {
			throw WRONG_ARGUMENT;
		}
	}

	public static String setForceWrapping(String value, boolean force) {
		if (value == null) {
			throw WRONG_ARGUMENT;
		}
		try {
			int existingValue = Integer.parseInt(value);
			// clear existing force bit
			existingValue &= ~Alignment2.M_FORCE;
			if (force) {
				existingValue |= Alignment2.M_FORCE;
			}
			return String.valueOf(existingValue);
		} catch (NumberFormatException e) {
			throw WRONG_ARGUMENT;
		}		
	}
	
	public static String setIndentStyle(String value, int indentStyle) {
		if (value == null) {
			throw WRONG_ARGUMENT;
		}
		switch(indentStyle) {
			case INDENT_BY_ONE :
			case INDENT_DEFAULT :
			case INDENT_ON_COLUMN :
				break;
			default :
				throw WRONG_ARGUMENT;
		}
		try {
			int existingValue = Integer.parseInt(value);
			// clear existing indent bits
			existingValue &= ~(Alignment2.M_INDENT_BY_ONE | Alignment2.M_INDENT_ON_COLUMN);
			switch(indentStyle) {
				case INDENT_BY_ONE :
					existingValue |= Alignment2.M_INDENT_BY_ONE;
					break;
				case INDENT_ON_COLUMN :
					existingValue |= Alignment2.M_INDENT_ON_COLUMN;
			}
			return String.valueOf(existingValue);
		} catch (NumberFormatException e) {
			throw WRONG_ARGUMENT;
		}
	}

	public static String setWrappingStyle(String value, int wrappingStyle) {
		if (value == null) {
			throw WRONG_ARGUMENT;
		}
		switch(wrappingStyle) {
			case WRAP_COMPACT :
			case WRAP_COMPACT_FIRST_BREAK :
			case WRAP_NEXT_PER_LINE :
			case WRAP_NEXT_SHIFTED :
			case WRAP_NO_SPLIT :
			case WRAP_ONE_PER_LINE :
				break;
			default:
				throw WRONG_ARGUMENT;
		}
		try {
			int existingValue = Integer.parseInt(value);
			// clear existing split bits
			existingValue &= ~(Alignment2.SPLIT_MASK);
			switch(wrappingStyle) {
				case WRAP_COMPACT :
					existingValue |= Alignment2.M_COMPACT_SPLIT;
					break;
				case WRAP_COMPACT_FIRST_BREAK :
					existingValue |= Alignment2.M_COMPACT_FIRST_BREAK_SPLIT;
					break;
				case WRAP_NEXT_PER_LINE :
					existingValue |= Alignment2.M_NEXT_PER_LINE_SPLIT;
					break;
				case WRAP_NEXT_SHIFTED :
					existingValue |= Alignment2.M_NEXT_SHIFTED_SPLIT;
					break;
				case WRAP_ONE_PER_LINE :
					existingValue |= Alignment2.M_ONE_PER_LINE_SPLIT;
					break;
			}
			return String.valueOf(existingValue);
		} catch (NumberFormatException e) {
			throw WRONG_ARGUMENT;
		}
	}
}
