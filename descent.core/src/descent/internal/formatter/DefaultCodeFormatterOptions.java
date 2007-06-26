package descent.internal.formatter;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;
import descent.internal.formatter.align.Alignment2;

public class DefaultCodeFormatterOptions
{	
	public enum TabChar
	{
		TAB(DefaultCodeFormatterConstants.TAB),
		SPACE(DefaultCodeFormatterConstants.SPACE),
		MIXED(DefaultCodeFormatterConstants.MIXED);
		
		private final String constVal;
		TabChar(String $constVal) { constVal = $constVal; }
		public String toString() { return constVal; }
	}
	
	public enum BracePosition
	{
		END_OF_LINE(DefaultCodeFormatterConstants.END_OF_LINE),
		NEXT_LINE(DefaultCodeFormatterConstants.NEXT_LINE),
		NEXT_LINE_SHIFTED(DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		
		private final String constVal;
		BracePosition(String $constVal) { constVal = $constVal; }
		public String toString() { return constVal; }
	}
	
	public static DefaultCodeFormatterOptions getDefaultSettings() {
		return getBuiltInProfile(DefaultCodeFormatterConstants.DEFAULT_PROFILE);
	}
	
	public static DefaultCodeFormatterOptions getBuiltInProfile(String name)
	{
		DefaultCodeFormatterOptions options = new DefaultCodeFormatterOptions();
		options.setDefaultSettings();
		return options;
	}
	
	// User formatting options
	public BracePosition brace_position_for_function_declaration;
	public BracePosition brace_position_for_type_declaration;
	public BracePosition brace_position_for_enum_declaration;
	public BracePosition brace_position_for_template_declaration;
	public BracePosition brace_position_for_conditional_declaration;
	public BracePosition brace_position_for_conditional_statement;
	public BracePosition brace_position_for_loop_statement;
	public BracePosition brace_position_for_function_literal;
	public BracePosition brace_position_for_anonymous_type;
	public BracePosition brace_position_for_switch_statement;
	public BracePosition brace_position_for_switch_case;
	public BracePosition brace_position_for_try_catch_finally;
	public BracePosition brace_position_for_modifiers;
	public BracePosition brace_position_for_synchronized_statement;
	public BracePosition brace_position_for_with_statement;
	public BracePosition brace_position_for_scope_statement;
	public BracePosition brace_position_for_other_blocks;
	public boolean insert_space_before_semicolon;
	public boolean insert_space_before_semicolon_in_for_statement;
	public boolean insert_space_after_semicolon_in_for_statement;
	public boolean insert_space_before_semicolon_in_foreach_statement;
	public boolean insert_space_after_semicolon_in_foreach_statement;
	public boolean insert_space_before_comma_in_multiple_field_declarations;
	public boolean insert_space_after_comma_in_multiple_field_declarations;
	public boolean insert_space_between_empty_parens_in_function_declaration;
	public boolean insert_space_before_opening_paren_in_function_declaration;
	public boolean insert_space_after_opening_paren_in_function_declaration;
	public boolean insert_space_before_closing_paren_in_function_declaration;
	public boolean insert_space_before_opening_paren_in_function_invocation;
	public boolean insert_space_after_opening_paren_in_function_invocation;
	public boolean insert_space_before_comma_in_function_invocation_arguments;
	public boolean insert_space_after_comma_in_function_invocation_arguments;
	public boolean insert_space_before_comma_in_foreach_statement;
	public boolean insert_space_after_comma_in_foreach_statement;
	public boolean insert_space_before_closing_paren_in_function_invocation;
	public boolean insert_space_between_empty_parens_in_function_invocation;
	public boolean insert_new_line_before_else;
	public boolean insert_new_line_before_catch;
	public boolean insert_new_line_before_finally;
	public boolean insert_new_line_before_while_in_do_statement;
	public int blank_lines_before_module;
	public int blank_lines_after_module;
	public boolean keep_simple_then_declaration_on_same_line;
	public boolean keep_simple_else_declaration_on_same_line;
	public boolean keep_simple_then_statement_on_same_line;
	public boolean keep_simple_else_statement_on_same_line;
	public boolean keep_simple_try_statement_on_same_line;
	public boolean keep_simple_catch_statement_on_same_line;
	public boolean keep_simple_finally_statement_on_same_line;
	public boolean keep_simple_loop_statement_on_same_line;
	public boolean keep_simple_switch_statement_on_same_line;
	public boolean keep_simple_synchronized_statement_on_same_line;
	public boolean keep_simple_while_statement_on_same_line;
	public boolean keep_simple_with_statement_on_same_line;
	public boolean insert_new_line_at_end_of_file_if_missing;
	public boolean keep_functions_with_no_statement_in_one_line;
	public boolean keep_functions_with_one_statement_in_one_line;
	public boolean keep_else_conditional_on_one_line;
	public int indentation_size;
	public int continuation_indentation;
	public boolean indent_empty_lines;
	public boolean indent_body_declarations_compare_to_type_header;
	public boolean indent_body_declarations_compare_to_template_header;
	public boolean indent_body_declarations_compare_to_modifier_header;
	public boolean indent_statements_compare_to_function_header;
	public boolean indent_in_out_body_compare_to_function_header;
	public boolean indent_statements_compare_to_function_in_header;
	public boolean indent_statements_compare_to_function_out_header;
	public boolean indent_statements_compare_to_function_body_header;
	public boolean indent_enum_members_compare_to_enum_header;
	public TabChar tab_char;
	public int tab_size;
	public boolean use_tabs_only_for_leading_indentations;
	public boolean never_indent_block_comments_on_first_column;
	public boolean never_indent_line_comments_on_first_column;
	public int line_split;
	public int page_width;
	public int number_of_empty_lines_to_preserve;
	
	// Set by the caller
	public String line_separator;
    public int initial_indentation_level;
	
	private DefaultCodeFormatterOptions() {
		// cannot be instantiated
	}

	public DefaultCodeFormatterOptions(Map settings) {
		setDefaultSettings();
		if (settings == null) return;
		set(settings);
	}

	private String getAlignment2(int alignment) {
		return Integer.toString(alignment);
	}

	public Map<String, String> getMap() {
		
		Map<String, String> options = new HashMap<String, String>();
		
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_FUNCTION_DECLARATION, brace_position_for_function_declaration.toString());
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION, brace_position_for_type_declaration.toString());
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ENUM_DECLARATION, brace_position_for_enum_declaration.toString());
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TEMPLATE_DECLARATION, brace_position_for_template_declaration.toString());
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CONDITIONAL_DECLARATION, brace_position_for_conditional_declaration.toString());
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CONDITIONAL_STATEMENT, brace_position_for_conditional_statement.toString());
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_LOOP_STATEMENT, brace_position_for_loop_statement.toString());
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_FUNCTION_LITERAL, brace_position_for_function_literal.toString());
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ANONYMOUS_TYPE, brace_position_for_anonymous_type.toString());
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH_STATEMENT, brace_position_for_switch_statement.toString());
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH_CASE, brace_position_for_switch_case.toString());
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TRY_CATCH_FINALLY, brace_position_for_try_catch_finally.toString());
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_MODIFIERS, brace_position_for_modifiers.toString());
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SYNCHRONIZED_STATEMENT, brace_position_for_synchronized_statement.toString());
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_WITH_STATEMENT, brace_position_for_with_statement.toString());
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SCOPE_STATEMENT, brace_position_for_scope_statement.toString());
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_OTHER_BLOCKS, brace_position_for_other_blocks.toString());
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON, insert_space_before_semicolon ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOR_STATEMENT, insert_space_before_semicolon_in_for_statement ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOR_STATEMENT, insert_space_after_semicolon_in_for_statement ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOREACH_STATEMENT, insert_space_before_semicolon_in_foreach_statement ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOREACH_STATEMENT, insert_space_after_semicolon_in_foreach_statement ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS, insert_space_before_comma_in_multiple_field_declarations ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS, insert_space_after_comma_in_multiple_field_declarations ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_FUNCTION_DECLARATION, insert_space_between_empty_parens_in_function_declaration ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_DECLARATION, insert_space_before_opening_paren_in_function_declaration ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_DECLARATION, insert_space_after_opening_paren_in_function_declaration ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FUNCTION_DECLARATION, insert_space_before_closing_paren_in_function_declaration ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_INVOCATION, insert_space_before_opening_paren_in_function_invocation ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_INVOCATION, insert_space_after_opening_paren_in_function_invocation ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FUNCTION_INVOCATION_ARGUMENTS, insert_space_before_comma_in_function_invocation_arguments ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FUNCTION_INVOCATION_ARGUMENTS, insert_space_after_comma_in_function_invocation_arguments ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FOREACH_STATEMENT, insert_space_before_comma_in_foreach_statement ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOREACH_STATEMENT, insert_space_after_comma_in_foreach_statement ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FUNCTION_INVOCATION, insert_space_before_closing_paren_in_function_invocation ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_FUNCTION_INVOCATION, insert_space_between_empty_parens_in_function_invocation ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE, insert_new_line_before_else ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_CATCH, insert_new_line_before_catch ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_FINALLY, insert_new_line_before_finally ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_WHILE_IN_DO_STATEMENT, insert_new_line_before_while_in_do_statement ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_MODULE, Integer.toString(blank_lines_before_module));
		options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AFTER_MODULE, Integer.toString(blank_lines_after_module));
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_THEN_DECLARATION_ON_SAME_LINE, keep_simple_then_declaration_on_same_line ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_ELSE_DECLARATION_ON_SAME_LINE, keep_simple_else_declaration_on_same_line ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_THEN_STATEMENT_ON_SAME_LINE, keep_simple_then_statement_on_same_line ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_ELSE_STATEMENT_ON_SAME_LINE, keep_simple_else_statement_on_same_line ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_TRY_STATEMENT_ON_SAME_LINE, keep_simple_try_statement_on_same_line ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_CATCH_STATEMENT_ON_SAME_LINE, keep_simple_catch_statement_on_same_line ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_FINALLY_STATEMENT_ON_SAME_LINE, keep_simple_finally_statement_on_same_line ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_LOOP_STATEMENT_ON_SAME_LINE, keep_simple_loop_statement_on_same_line ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_SWITCH_STATEMENT_ON_SAME_LINE, keep_simple_switch_statement_on_same_line ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_SYNCHRONIZED_STATEMENT_ON_SAME_LINE, keep_simple_synchronized_statement_on_same_line ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_WHILE_STATEMENT_ON_SAME_LINE, keep_simple_while_statement_on_same_line ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_WITH_STATEMENT_ON_SAME_LINE, keep_simple_with_statement_on_same_line ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AT_END_OF_FILE_IF_MISSING, insert_new_line_at_end_of_file_if_missing ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_FUNCTIONS_WITH_NO_STATEMENT_IN_ONE_LINE, keep_functions_with_no_statement_in_one_line ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_FUNCTIONS_WITH_ONE_STATEMENT_IN_ONE_LINE, keep_functions_with_one_statement_in_one_line ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_ELSE_CONDITIONAL_ON_ONE_LINE, keep_else_conditional_on_one_line ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE, Integer.toString(indentation_size));
		options.put(DefaultCodeFormatterConstants.FORMATTER_CONTINUATION_INDENTATION, Integer.toString(continuation_indentation));
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_EMPTY_LINES, indent_empty_lines ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_TYPE_HEADER, indent_body_declarations_compare_to_type_header ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_TEMPLATE_HEADER, indent_body_declarations_compare_to_template_header ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_MODIFIER_HEADER, indent_body_declarations_compare_to_modifier_header ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_HEADER, indent_statements_compare_to_function_header ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_IN_OUT_BODY_COMPARE_TO_FUNCTION_HEADER, indent_in_out_body_compare_to_function_header ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_IN_HEADER, indent_statements_compare_to_function_in_header ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_OUT_HEADER, indent_statements_compare_to_function_out_header ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_BODY_HEADER, indent_statements_compare_to_function_body_header ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_ENUM_MEMBERS_COMPARE_TO_ENUM_HEADER, indent_enum_members_compare_to_enum_header ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, tab_char.toString());
		options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, Integer.toString(tab_size));
		options.put(DefaultCodeFormatterConstants.FORMATTER_USE_TABS_ONLY_FOR_LEADING_INDENTATIONS, use_tabs_only_for_leading_indentations ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_NEVER_INDENT_BLOCK_COMMENTS_ON_FIRST_COLUMN, never_indent_block_comments_on_first_column ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_NEVER_INDENT_LINE_COMMENTS_ON_FIRST_COLUMN, never_indent_line_comments_on_first_column ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_LINE_SPLIT, Integer.toString(line_split));
		options.put(DefaultCodeFormatterConstants.FORMATTER_PAGE_WIDTH, Integer.toString(page_width));
		options.put(DefaultCodeFormatterConstants.FORMATTER_NUMBER_OF_EMPTY_LINES_TO_PRESERVE, Integer.toString(number_of_empty_lines_to_preserve));
		 
		return options;
	}

	public void set(Map<String, String> settings) {
		
		String current;
		
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_FUNCTION_DECLARATION);
		if(null != current) {
			try {
				brace_position_for_function_declaration = DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED.equals(current) ? BracePosition.NEXT_LINE_SHIFTED : DefaultCodeFormatterConstants.NEXT_LINE.equals(current) ? BracePosition.NEXT_LINE : BracePosition.END_OF_LINE;
			} catch(Exception e) {
				brace_position_for_function_declaration = BracePosition.END_OF_LINE;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION);
		if(null != current) {
			try {
				brace_position_for_type_declaration = DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED.equals(current) ? BracePosition.NEXT_LINE_SHIFTED : DefaultCodeFormatterConstants.NEXT_LINE.equals(current) ? BracePosition.NEXT_LINE : BracePosition.END_OF_LINE;
			} catch(Exception e) {
				brace_position_for_type_declaration = BracePosition.END_OF_LINE;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ENUM_DECLARATION);
		if(null != current) {
			try {
				brace_position_for_enum_declaration = DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED.equals(current) ? BracePosition.NEXT_LINE_SHIFTED : DefaultCodeFormatterConstants.NEXT_LINE.equals(current) ? BracePosition.NEXT_LINE : BracePosition.END_OF_LINE;
			} catch(Exception e) {
				brace_position_for_enum_declaration = BracePosition.END_OF_LINE;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TEMPLATE_DECLARATION);
		if(null != current) {
			try {
				brace_position_for_template_declaration = DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED.equals(current) ? BracePosition.NEXT_LINE_SHIFTED : DefaultCodeFormatterConstants.NEXT_LINE.equals(current) ? BracePosition.NEXT_LINE : BracePosition.END_OF_LINE;
			} catch(Exception e) {
				brace_position_for_template_declaration = BracePosition.END_OF_LINE;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CONDITIONAL_DECLARATION);
		if(null != current) {
			try {
				brace_position_for_conditional_declaration = DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED.equals(current) ? BracePosition.NEXT_LINE_SHIFTED : DefaultCodeFormatterConstants.NEXT_LINE.equals(current) ? BracePosition.NEXT_LINE : BracePosition.END_OF_LINE;
			} catch(Exception e) {
				brace_position_for_conditional_declaration = BracePosition.END_OF_LINE;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CONDITIONAL_STATEMENT);
		if(null != current) {
			try {
				brace_position_for_conditional_statement = DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED.equals(current) ? BracePosition.NEXT_LINE_SHIFTED : DefaultCodeFormatterConstants.NEXT_LINE.equals(current) ? BracePosition.NEXT_LINE : BracePosition.END_OF_LINE;
			} catch(Exception e) {
				brace_position_for_conditional_statement = BracePosition.END_OF_LINE;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_LOOP_STATEMENT);
		if(null != current) {
			try {
				brace_position_for_loop_statement = DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED.equals(current) ? BracePosition.NEXT_LINE_SHIFTED : DefaultCodeFormatterConstants.NEXT_LINE.equals(current) ? BracePosition.NEXT_LINE : BracePosition.END_OF_LINE;
			} catch(Exception e) {
				brace_position_for_loop_statement = BracePosition.END_OF_LINE;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_FUNCTION_LITERAL);
		if(null != current) {
			try {
				brace_position_for_function_literal = DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED.equals(current) ? BracePosition.NEXT_LINE_SHIFTED : DefaultCodeFormatterConstants.NEXT_LINE.equals(current) ? BracePosition.NEXT_LINE : BracePosition.END_OF_LINE;
			} catch(Exception e) {
				brace_position_for_function_literal = BracePosition.END_OF_LINE;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ANONYMOUS_TYPE);
		if(null != current) {
			try {
				brace_position_for_anonymous_type = DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED.equals(current) ? BracePosition.NEXT_LINE_SHIFTED : DefaultCodeFormatterConstants.NEXT_LINE.equals(current) ? BracePosition.NEXT_LINE : BracePosition.END_OF_LINE;
			} catch(Exception e) {
				brace_position_for_anonymous_type = BracePosition.END_OF_LINE;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH_STATEMENT);
		if(null != current) {
			try {
				brace_position_for_switch_statement = DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED.equals(current) ? BracePosition.NEXT_LINE_SHIFTED : DefaultCodeFormatterConstants.NEXT_LINE.equals(current) ? BracePosition.NEXT_LINE : BracePosition.END_OF_LINE;
			} catch(Exception e) {
				brace_position_for_switch_statement = BracePosition.END_OF_LINE;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH_CASE);
		if(null != current) {
			try {
				brace_position_for_switch_case = DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED.equals(current) ? BracePosition.NEXT_LINE_SHIFTED : DefaultCodeFormatterConstants.NEXT_LINE.equals(current) ? BracePosition.NEXT_LINE : BracePosition.END_OF_LINE;
			} catch(Exception e) {
				brace_position_for_switch_case = BracePosition.END_OF_LINE;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TRY_CATCH_FINALLY);
		if(null != current) {
			try {
				brace_position_for_try_catch_finally = DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED.equals(current) ? BracePosition.NEXT_LINE_SHIFTED : DefaultCodeFormatterConstants.NEXT_LINE.equals(current) ? BracePosition.NEXT_LINE : BracePosition.END_OF_LINE;
			} catch(Exception e) {
				brace_position_for_try_catch_finally = BracePosition.END_OF_LINE;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_MODIFIERS);
		if(null != current) {
			try {
				brace_position_for_modifiers = DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED.equals(current) ? BracePosition.NEXT_LINE_SHIFTED : DefaultCodeFormatterConstants.NEXT_LINE.equals(current) ? BracePosition.NEXT_LINE : BracePosition.END_OF_LINE;
			} catch(Exception e) {
				brace_position_for_modifiers = BracePosition.END_OF_LINE;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SYNCHRONIZED_STATEMENT);
		if(null != current) {
			try {
				brace_position_for_synchronized_statement = DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED.equals(current) ? BracePosition.NEXT_LINE_SHIFTED : DefaultCodeFormatterConstants.NEXT_LINE.equals(current) ? BracePosition.NEXT_LINE : BracePosition.END_OF_LINE;
			} catch(Exception e) {
				brace_position_for_synchronized_statement = BracePosition.END_OF_LINE;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_WITH_STATEMENT);
		if(null != current) {
			try {
				brace_position_for_with_statement = DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED.equals(current) ? BracePosition.NEXT_LINE_SHIFTED : DefaultCodeFormatterConstants.NEXT_LINE.equals(current) ? BracePosition.NEXT_LINE : BracePosition.END_OF_LINE;
			} catch(Exception e) {
				brace_position_for_with_statement = BracePosition.END_OF_LINE;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SCOPE_STATEMENT);
		if(null != current) {
			try {
				brace_position_for_scope_statement = DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED.equals(current) ? BracePosition.NEXT_LINE_SHIFTED : DefaultCodeFormatterConstants.NEXT_LINE.equals(current) ? BracePosition.NEXT_LINE : BracePosition.END_OF_LINE;
			} catch(Exception e) {
				brace_position_for_scope_statement = BracePosition.END_OF_LINE;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_OTHER_BLOCKS);
		if(null != current) {
			try {
				brace_position_for_other_blocks = DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED.equals(current) ? BracePosition.NEXT_LINE_SHIFTED : DefaultCodeFormatterConstants.NEXT_LINE.equals(current) ? BracePosition.NEXT_LINE : BracePosition.END_OF_LINE;
			} catch(Exception e) {
				brace_position_for_other_blocks = BracePosition.END_OF_LINE;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON);
		if(null != current) {
			try {
				insert_space_before_semicolon = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_space_before_semicolon = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOR_STATEMENT);
		if(null != current) {
			try {
				insert_space_before_semicolon_in_for_statement = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_space_before_semicolon_in_for_statement = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOR_STATEMENT);
		if(null != current) {
			try {
				insert_space_after_semicolon_in_for_statement = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_space_after_semicolon_in_for_statement = true;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOREACH_STATEMENT);
		if(null != current) {
			try {
				insert_space_before_semicolon_in_foreach_statement = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_space_before_semicolon_in_foreach_statement = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOREACH_STATEMENT);
		if(null != current) {
			try {
				insert_space_after_semicolon_in_foreach_statement = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_space_after_semicolon_in_foreach_statement = true;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS);
		if(null != current) {
			try {
				insert_space_before_comma_in_multiple_field_declarations = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_space_before_comma_in_multiple_field_declarations = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS);
		if(null != current) {
			try {
				insert_space_after_comma_in_multiple_field_declarations = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_space_after_comma_in_multiple_field_declarations = true;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_FUNCTION_DECLARATION);
		if(null != current) {
			try {
				insert_space_between_empty_parens_in_function_declaration = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_space_between_empty_parens_in_function_declaration = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_DECLARATION);
		if(null != current) {
			try {
				insert_space_before_opening_paren_in_function_declaration = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_space_before_opening_paren_in_function_declaration = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_DECLARATION);
		if(null != current) {
			try {
				insert_space_after_opening_paren_in_function_declaration = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_space_after_opening_paren_in_function_declaration = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FUNCTION_DECLARATION);
		if(null != current) {
			try {
				insert_space_before_closing_paren_in_function_declaration = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_space_before_closing_paren_in_function_declaration = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_INVOCATION);
		if(null != current) {
			try {
				insert_space_before_opening_paren_in_function_invocation = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_space_before_opening_paren_in_function_invocation = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_INVOCATION);
		if(null != current) {
			try {
				insert_space_after_opening_paren_in_function_invocation = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_space_after_opening_paren_in_function_invocation = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FUNCTION_INVOCATION_ARGUMENTS);
		if(null != current) {
			try {
				insert_space_before_comma_in_function_invocation_arguments = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_space_before_comma_in_function_invocation_arguments = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FUNCTION_INVOCATION_ARGUMENTS);
		if(null != current) {
			try {
				insert_space_after_comma_in_function_invocation_arguments = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_space_after_comma_in_function_invocation_arguments = true;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FOREACH_STATEMENT);
		if(null != current) {
			try {
				insert_space_before_comma_in_foreach_statement = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_space_before_comma_in_foreach_statement = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOREACH_STATEMENT);
		if(null != current) {
			try {
				insert_space_after_comma_in_foreach_statement = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_space_after_comma_in_foreach_statement = true;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FUNCTION_INVOCATION);
		if(null != current) {
			try {
				insert_space_before_closing_paren_in_function_invocation = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_space_before_closing_paren_in_function_invocation = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_FUNCTION_INVOCATION);
		if(null != current) {
			try {
				insert_space_between_empty_parens_in_function_invocation = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_space_between_empty_parens_in_function_invocation = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE);
		if(null != current) {
			try {
				insert_new_line_before_else = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_new_line_before_else = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_CATCH);
		if(null != current) {
			try {
				insert_new_line_before_catch = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_new_line_before_catch = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_FINALLY);
		if(null != current) {
			try {
				insert_new_line_before_finally = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_new_line_before_finally = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_WHILE_IN_DO_STATEMENT);
		if(null != current) {
			try {
				insert_new_line_before_while_in_do_statement = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_new_line_before_while_in_do_statement = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_MODULE);
		if(null != current) {
			try {
				blank_lines_before_module = Integer.parseInt(current);
			} catch(Exception e) {
				blank_lines_before_module = 0;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AFTER_MODULE);
		if(null != current) {
			try {
				blank_lines_after_module = Integer.parseInt(current);
			} catch(Exception e) {
				blank_lines_after_module = 1;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_THEN_DECLARATION_ON_SAME_LINE);
		if(null != current) {
			try {
				keep_simple_then_declaration_on_same_line = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				keep_simple_then_declaration_on_same_line = true;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_ELSE_DECLARATION_ON_SAME_LINE);
		if(null != current) {
			try {
				keep_simple_else_declaration_on_same_line = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				keep_simple_else_declaration_on_same_line = true;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_THEN_STATEMENT_ON_SAME_LINE);
		if(null != current) {
			try {
				keep_simple_then_statement_on_same_line = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				keep_simple_then_statement_on_same_line = true;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_ELSE_STATEMENT_ON_SAME_LINE);
		if(null != current) {
			try {
				keep_simple_else_statement_on_same_line = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				keep_simple_else_statement_on_same_line = true;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_TRY_STATEMENT_ON_SAME_LINE);
		if(null != current) {
			try {
				keep_simple_try_statement_on_same_line = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				keep_simple_try_statement_on_same_line = true;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_CATCH_STATEMENT_ON_SAME_LINE);
		if(null != current) {
			try {
				keep_simple_catch_statement_on_same_line = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				keep_simple_catch_statement_on_same_line = true;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_FINALLY_STATEMENT_ON_SAME_LINE);
		if(null != current) {
			try {
				keep_simple_finally_statement_on_same_line = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				keep_simple_finally_statement_on_same_line = true;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_LOOP_STATEMENT_ON_SAME_LINE);
		if(null != current) {
			try {
				keep_simple_loop_statement_on_same_line = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				keep_simple_loop_statement_on_same_line = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_SWITCH_STATEMENT_ON_SAME_LINE);
		if(null != current) {
			try {
				keep_simple_switch_statement_on_same_line = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				keep_simple_switch_statement_on_same_line = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_SYNCHRONIZED_STATEMENT_ON_SAME_LINE);
		if(null != current) {
			try {
				keep_simple_synchronized_statement_on_same_line = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				keep_simple_synchronized_statement_on_same_line = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_WHILE_STATEMENT_ON_SAME_LINE);
		if(null != current) {
			try {
				keep_simple_while_statement_on_same_line = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				keep_simple_while_statement_on_same_line = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_WITH_STATEMENT_ON_SAME_LINE);
		if(null != current) {
			try {
				keep_simple_with_statement_on_same_line = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				keep_simple_with_statement_on_same_line = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AT_END_OF_FILE_IF_MISSING);
		if(null != current) {
			try {
				insert_new_line_at_end_of_file_if_missing = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_new_line_at_end_of_file_if_missing = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_KEEP_FUNCTIONS_WITH_NO_STATEMENT_IN_ONE_LINE);
		if(null != current) {
			try {
				keep_functions_with_no_statement_in_one_line = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				keep_functions_with_no_statement_in_one_line = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_KEEP_FUNCTIONS_WITH_ONE_STATEMENT_IN_ONE_LINE);
		if(null != current) {
			try {
				keep_functions_with_one_statement_in_one_line = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				keep_functions_with_one_statement_in_one_line = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_KEEP_ELSE_CONDITIONAL_ON_ONE_LINE);
		if(null != current) {
			try {
				keep_else_conditional_on_one_line = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				keep_else_conditional_on_one_line = true;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE);
		if(null != current) {
			try {
				indentation_size = Integer.parseInt(current);
			} catch(Exception e) {
				indentation_size = 4;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_CONTINUATION_INDENTATION);
		if(null != current) {
			try {
				continuation_indentation = Integer.parseInt(current);
			} catch(Exception e) {
				continuation_indentation = 2;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INDENT_EMPTY_LINES);
		if(null != current) {
			try {
				indent_empty_lines = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				indent_empty_lines = true;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_TYPE_HEADER);
		if(null != current) {
			try {
				indent_body_declarations_compare_to_type_header = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				indent_body_declarations_compare_to_type_header = true;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_TEMPLATE_HEADER);
		if(null != current) {
			try {
				indent_body_declarations_compare_to_template_header = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				indent_body_declarations_compare_to_template_header = true;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_MODIFIER_HEADER);
		if(null != current) {
			try {
				indent_body_declarations_compare_to_modifier_header = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				indent_body_declarations_compare_to_modifier_header = true;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_HEADER);
		if(null != current) {
			try {
				indent_statements_compare_to_function_header = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				indent_statements_compare_to_function_header = true;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INDENT_IN_OUT_BODY_COMPARE_TO_FUNCTION_HEADER);
		if(null != current) {
			try {
				indent_in_out_body_compare_to_function_header = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				indent_in_out_body_compare_to_function_header = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_IN_HEADER);
		if(null != current) {
			try {
				indent_statements_compare_to_function_in_header = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				indent_statements_compare_to_function_in_header = indent_statements_compare_to_function_header;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_OUT_HEADER);
		if(null != current) {
			try {
				indent_statements_compare_to_function_out_header = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				indent_statements_compare_to_function_out_header = true;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_BODY_HEADER);
		if(null != current) {
			try {
				indent_statements_compare_to_function_body_header = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				indent_statements_compare_to_function_body_header = indent_statements_compare_to_function_header;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INDENT_ENUM_MEMBERS_COMPARE_TO_ENUM_HEADER);
		if(null != current) {
			try {
				indent_enum_members_compare_to_enum_header = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				indent_enum_members_compare_to_enum_header = true;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR);
		if(null != current) {
			try {
				tab_char = DefaultCodeFormatterConstants.MIXED.equals(current) ? TabChar.MIXED : DefaultCodeFormatterConstants.SPACE.equals(current) ? TabChar.SPACE : TabChar.TAB;
			} catch(Exception e) {
				tab_char = TabChar.TAB;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
		if(null != current) {
			try {
				tab_size = Integer.parseInt(current);
			} catch(Exception e) {
				tab_size = 4;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_USE_TABS_ONLY_FOR_LEADING_INDENTATIONS);
		if(null != current) {
			try {
				use_tabs_only_for_leading_indentations = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				use_tabs_only_for_leading_indentations = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_NEVER_INDENT_BLOCK_COMMENTS_ON_FIRST_COLUMN);
		if(null != current) {
			try {
				never_indent_block_comments_on_first_column = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				never_indent_block_comments_on_first_column = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_NEVER_INDENT_LINE_COMMENTS_ON_FIRST_COLUMN);
		if(null != current) {
			try {
				never_indent_line_comments_on_first_column = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				never_indent_line_comments_on_first_column = false;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_LINE_SPLIT);
		if(null != current) {
			try {
				line_split = Integer.parseInt(current);
			} catch(Exception e) {
				line_split = 9999;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_PAGE_WIDTH);
		if(null != current) {
			try {
				page_width = Integer.parseInt(current);
			} catch(Exception e) {
				page_width = 80;
			}
		}
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_NUMBER_OF_EMPTY_LINES_TO_PRESERVE);
		if(null != current) {
			try {
				number_of_empty_lines_to_preserve = Integer.parseInt(current);
			} catch(Exception e) {
				number_of_empty_lines_to_preserve = 1;
			}
		}
	}

	public void setDefaultSettings() {
		brace_position_for_function_declaration = BracePosition.END_OF_LINE;
		brace_position_for_type_declaration = BracePosition.END_OF_LINE;
		brace_position_for_enum_declaration = BracePosition.END_OF_LINE;
		brace_position_for_template_declaration = BracePosition.END_OF_LINE;
		brace_position_for_conditional_declaration = BracePosition.END_OF_LINE;
		brace_position_for_conditional_statement = BracePosition.END_OF_LINE;
		brace_position_for_loop_statement = BracePosition.END_OF_LINE;
		brace_position_for_function_literal = BracePosition.END_OF_LINE;
		brace_position_for_anonymous_type = BracePosition.END_OF_LINE;
		brace_position_for_switch_statement = BracePosition.END_OF_LINE;
		brace_position_for_switch_case = BracePosition.END_OF_LINE;
		brace_position_for_try_catch_finally = BracePosition.END_OF_LINE;
		brace_position_for_modifiers = BracePosition.END_OF_LINE;
		brace_position_for_synchronized_statement = BracePosition.END_OF_LINE;
		brace_position_for_with_statement = BracePosition.END_OF_LINE;
		brace_position_for_scope_statement = BracePosition.END_OF_LINE;
		brace_position_for_other_blocks = BracePosition.END_OF_LINE;
		insert_space_before_semicolon = false;
		insert_space_before_semicolon_in_for_statement = false;
		insert_space_after_semicolon_in_for_statement = true;
		insert_space_before_semicolon_in_foreach_statement = false;
		insert_space_after_semicolon_in_foreach_statement = true;
		insert_space_before_comma_in_multiple_field_declarations = false;
		insert_space_after_comma_in_multiple_field_declarations = true;
		insert_space_between_empty_parens_in_function_declaration = false;
		insert_space_before_opening_paren_in_function_declaration = false;
		insert_space_after_opening_paren_in_function_declaration = false;
		insert_space_before_closing_paren_in_function_declaration = false;
		insert_space_before_opening_paren_in_function_invocation = false;
		insert_space_after_opening_paren_in_function_invocation = false;
		insert_space_before_comma_in_function_invocation_arguments = false;
		insert_space_after_comma_in_function_invocation_arguments = true;
		insert_space_before_comma_in_foreach_statement = false;
		insert_space_after_comma_in_foreach_statement = true;
		insert_space_before_closing_paren_in_function_invocation = false;
		insert_space_between_empty_parens_in_function_invocation = false;
		insert_new_line_before_else = false;
		insert_new_line_before_catch = false;
		insert_new_line_before_finally = false;
		insert_new_line_before_while_in_do_statement = false;
		blank_lines_before_module = 0;
		blank_lines_after_module = 1;
		keep_simple_then_declaration_on_same_line = true;
		keep_simple_else_declaration_on_same_line = true;
		keep_simple_then_statement_on_same_line = true;
		keep_simple_else_statement_on_same_line = true;
		keep_simple_try_statement_on_same_line = true;
		keep_simple_catch_statement_on_same_line = true;
		keep_simple_finally_statement_on_same_line = true;
		keep_simple_loop_statement_on_same_line = false;
		keep_simple_switch_statement_on_same_line = false;
		keep_simple_synchronized_statement_on_same_line = false;
		keep_simple_while_statement_on_same_line = false;
		keep_simple_with_statement_on_same_line = false;
		insert_new_line_at_end_of_file_if_missing = false;
		keep_functions_with_no_statement_in_one_line = false;
		keep_functions_with_one_statement_in_one_line = false;
		keep_else_conditional_on_one_line = true;
		indentation_size = 4;
		continuation_indentation = 2;
		indent_empty_lines = true;
		indent_body_declarations_compare_to_type_header = true;
		indent_body_declarations_compare_to_template_header = true;
		indent_body_declarations_compare_to_modifier_header = true;
		indent_statements_compare_to_function_header = true;
		indent_in_out_body_compare_to_function_header = false;
		indent_statements_compare_to_function_in_header = indent_statements_compare_to_function_header;
		indent_statements_compare_to_function_out_header = true;
		indent_statements_compare_to_function_body_header = indent_statements_compare_to_function_header;
		indent_enum_members_compare_to_enum_header = true;
		tab_char = TabChar.TAB;
		tab_size = 4;
		use_tabs_only_for_leading_indentations = false;
		never_indent_block_comments_on_first_column = false;
		never_indent_line_comments_on_first_column = false;
		line_split = 9999;
		page_width = 80;
		number_of_empty_lines_to_preserve = 1;
	}
}
