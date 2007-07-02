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
	public static final String FORMATTER_BRACE_POSITION_FOR_FUNCTION_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_function_declaration";
	public static final String FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_type_declaration";
	public static final String FORMATTER_BRACE_POSITION_FOR_ENUM_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_enum_declaration";
	public static final String FORMATTER_BRACE_POSITION_FOR_TEMPLATE_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_template_declaration";
	public static final String FORMATTER_BRACE_POSITION_FOR_CONDITIONAL_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_conditional_declaration";
	public static final String FORMATTER_BRACE_POSITION_FOR_CONDITIONAL_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_conditional_statement";
	public static final String FORMATTER_BRACE_POSITION_FOR_LOOP_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_loop_statement";
	public static final String FORMATTER_BRACE_POSITION_FOR_FUNCTION_LITERAL = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_function_literal";
	public static final String FORMATTER_BRACE_POSITION_FOR_ANONYMOUS_TYPE = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_anonymous_type";
	public static final String FORMATTER_BRACE_POSITION_FOR_SWITCH_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_switch_statement";
	public static final String FORMATTER_BRACE_POSITION_FOR_SWITCH_CASE = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_switch_case";
	public static final String FORMATTER_BRACE_POSITION_FOR_TRY_CATCH_FINALLY = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_try_catch_finally";
	public static final String FORMATTER_BRACE_POSITION_FOR_MODIFIERS = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_modifiers";
	public static final String FORMATTER_BRACE_POSITION_FOR_SYNCHRONIZED_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_synchronized_statement";
	public static final String FORMATTER_BRACE_POSITION_FOR_WITH_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_with_statement";
	public static final String FORMATTER_BRACE_POSITION_FOR_SCOPE_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_scope_statement";
	public static final String FORMATTER_BRACE_POSITION_FOR_OTHER_BLOCKS = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_other_blocks";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_semicolon";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOR_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_semicolon_in_for_statement";
	public static final String FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOR_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_semicolon_in_for_statement";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOREACH_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_semicolon_in_foreach_statement";
	public static final String FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOREACH_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_semicolon_in_foreach_statement";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_multiple_field_declarations";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_multiple_field_declarations";
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_FUNCTION_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_between_empty_parens_in_function_declaration";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_function_declaration";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_function_declaration";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FUNCTION_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_function_declaration";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_INVOCATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_function_invocation";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_INVOCATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_function_invocation";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FUNCTION_INVOCATION_ARGUMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_function_invocation_arguments";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FUNCTION_INVOCATION_ARGUMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_function_invocation_arguments";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FOREACH_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_foreach_statement";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOREACH_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_foreach_statement";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FUNCTION_INVOCATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_function_invocation";
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_FUNCTION_INVOCATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_between_empty_parens_in_function_invocation";
	public static final String FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE = JavaCore.PLUGIN_ID + ".formatter.insert_new_line_before_else";
	public static final String FORMATTER_INSERT_NEW_LINE_BEFORE_CATCH = JavaCore.PLUGIN_ID + ".formatter.insert_new_line_before_catch";
	public static final String FORMATTER_INSERT_NEW_LINE_BEFORE_FINALLY = JavaCore.PLUGIN_ID + ".formatter.insert_new_line_before_finally";
	public static final String FORMATTER_INSERT_NEW_LINE_BEFORE_WHILE_IN_DO_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_new_line_before_while_in_do_statement";
	public static final String FORMATTER_INSERT_NEW_LINE_AT_END_OF_FILE_IF_MISSING = JavaCore.PLUGIN_ID + ".formatter.insert_new_line_at_end_of_file_if_missing";
	public static final String FORMATTER_BLANK_LINES_BEFORE_MODULE = JavaCore.PLUGIN_ID + ".formatter.blank_lines_before_module";
	public static final String FORMATTER_BLANK_LINES_AFTER_MODULE = JavaCore.PLUGIN_ID + ".formatter.blank_lines_after_module";
	public static final String FORMATTER_NUMBER_OF_EMPTY_LINES_TO_PRESERVE = JavaCore.PLUGIN_ID + ".formatter.number_of_empty_lines_to_preserve";
	public static final String FORMATTER_KEEP_SIMPLE_THEN_DECLARATION_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_then_declaration_on_same_line";
	public static final String FORMATTER_KEEP_SIMPLE_ELSE_DECLARATION_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_else_declaration_on_same_line";
	public static final String FORMATTER_KEEP_SIMPLE_THEN_STATEMENT_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_then_statement_on_same_line";
	public static final String FORMATTER_KEEP_SIMPLE_ELSE_STATEMENT_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_else_statement_on_same_line";
	public static final String FORMATTER_KEEP_SIMPLE_TRY_STATEMENT_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_try_statement_on_same_line";
	public static final String FORMATTER_KEEP_SIMPLE_CATCH_STATEMENT_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_catch_statement_on_same_line";
	public static final String FORMATTER_KEEP_SIMPLE_FINALLY_STATEMENT_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_finally_statement_on_same_line";
	public static final String FORMATTER_KEEP_SIMPLE_LOOP_STATEMENT_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_loop_statement_on_same_line";
	public static final String FORMATTER_KEEP_SIMPLE_SYNCHRONIZED_STATEMENT_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_synchronized_statement_on_same_line";
	public static final String FORMATTER_KEEP_SIMPLE_WITH_STATEMENT_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_with_statement_on_same_line";
	public static final String FORMATTER_KEEP_FUNCTIONS_WITH_NO_STATEMENT_IN_ONE_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_functions_with_no_statement_in_one_line";
	public static final String FORMATTER_KEEP_FUNCTIONS_WITH_ONE_STATEMENT_IN_ONE_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_functions_with_one_statement_in_one_line";
	public static final String FORMATTER_KEEP_ELSE_CONDITIONAL_ON_ONE_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_else_conditional_on_one_line";
	public static final String FORMATTER_KEEP_SIMPLE_SWITCH_STATEMENT_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_switch_statement_on_same_line";
	public static final String FORMATTER_INDENTATION_SIZE = JavaCore.PLUGIN_ID + ".formatter.indentation_size";
	public static final String FORMATTER_CONTINUATION_INDENTATION = JavaCore.PLUGIN_ID + ".formatter.continuation_indentation";
	public static final String FORMATTER_INDENT_EMPTY_LINES = JavaCore.PLUGIN_ID + ".formatter.indent_empty_lines";
	public static final String FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_TYPE_HEADER = JavaCore.PLUGIN_ID + ".formatter.indent_body_declarations_compare_to_type_header";
	public static final String FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_TEMPLATE_HEADER = JavaCore.PLUGIN_ID + ".formatter.indent_body_declarations_compare_to_template_header";
	public static final String FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_MODIFIER_HEADER = JavaCore.PLUGIN_ID + ".formatter.indent_body_declarations_compare_to_modifier_header";
	public static final String FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_HEADER = JavaCore.PLUGIN_ID + ".formatter.indent_statements_compare_to_function_header";
	public static final String FORMATTER_INDENT_IN_OUT_BODY_COMPARE_TO_FUNCTION_HEADER = JavaCore.PLUGIN_ID + ".formatter.indent_in_out_body_compare_to_function_header";
	public static final String FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_IN_HEADER = JavaCore.PLUGIN_ID + ".formatter.indent_statements_compare_to_function_in_header";
	public static final String FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_OUT_HEADER = JavaCore.PLUGIN_ID + ".formatter.indent_statements_compare_to_function_out_header";
	public static final String FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_BODY_HEADER = JavaCore.PLUGIN_ID + ".formatter.indent_statements_compare_to_function_body_header";
	public static final String FORMATTER_INDENT_ENUM_MEMBERS_COMPARE_TO_ENUM_HEADER = JavaCore.PLUGIN_ID + ".formatter.indent_enum_members_compare_to_enum_header";
	public static final String FORMATTER_TAB_CHAR = JavaCore.PLUGIN_ID + ".formatter.tab_char";
	public static final String FORMATTER_TAB_SIZE = JavaCore.PLUGIN_ID + ".formatter.tab_size";
	public static final String FORMATTER_USE_TABS_ONLY_FOR_LEADING_INDENTATIONS = JavaCore.PLUGIN_ID + ".formatter.use_tabs_only_for_leading_indentations";
	public static final String FORMATTER_NEVER_INDENT_BLOCK_COMMENTS_ON_FIRST_COLUMN = JavaCore.PLUGIN_ID + ".formatter.never_indent_block_comments_on_first_column";
	public static final String FORMATTER_NEVER_INDENT_LINE_COMMENTS_ON_FIRST_COLUMN = JavaCore.PLUGIN_ID + ".formatter.never_indent_line_comments_on_first_column";
	public static final String FORMATTER_LINE_SPLIT = JavaCore.PLUGIN_ID + ".formatter.line_split";
	public static final String FORMATTER_PAGE_WIDTH = JavaCore.PLUGIN_ID + ".formatter.page_width";
	
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
