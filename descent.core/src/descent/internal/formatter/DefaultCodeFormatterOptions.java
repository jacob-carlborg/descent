package descent.internal.formatter;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;
import descent.internal.formatter.align.Alignment2;

public class DefaultCodeFormatterOptions
{
	// TODO different default profiles? (or just get rid of the unused ones)
	
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
		DefaultCodeFormatterOptions options = new DefaultCodeFormatterOptions();
		options.setDefaultSettings();
		return options;
	}
	
	public static DefaultCodeFormatterOptions getEclipseDefaultSettings() {
		DefaultCodeFormatterOptions options = new DefaultCodeFormatterOptions();
		options.setDefaultSettings();
		return options;
	}

	public static DefaultCodeFormatterOptions getJavaConventionsSettings() {
		DefaultCodeFormatterOptions options = new DefaultCodeFormatterOptions();
		options.setDefaultSettings();
		return options;
	}
	
	// User formatting options
	public BracePosition brace_position_for_function_declaration;
	public boolean insert_space_before_semicolon;
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
	public boolean insert_space_before_closing_paren_in_function_invocation;
	public boolean insert_space_between_empty_parens_in_function_invocation;
	public boolean insert_new_line_at_end_of_file_if_missing;
	public int blank_lines_before_module;
	public int blank_lines_after_module;
	public int line_split;
	public boolean never_indent_block_comments_on_first_column;
	public boolean never_indent_line_comments_on_first_column;
	public int page_width;
	public int tab_size;
	public boolean use_tabs_only_for_leading_indentations;
	public boolean indent_empty_lines;
	public TabChar tab_char;
	public int indentation_size;
	public int continuation_indentation;
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
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON, insert_space_before_semicolon ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
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
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FUNCTION_INVOCATION, insert_space_before_closing_paren_in_function_invocation ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_FUNCTION_INVOCATION, insert_space_between_empty_parens_in_function_invocation ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AT_END_OF_FILE_IF_MISSING, insert_new_line_at_end_of_file_if_missing ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_MODULE, Integer.toString(blank_lines_before_module));
		options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AFTER_MODULE, Integer.toString(blank_lines_after_module));
		options.put(DefaultCodeFormatterConstants.FORMATTER_LINE_SPLIT, Integer.toString(line_split));
		options.put(DefaultCodeFormatterConstants.FORMATTER_NEVER_INDENT_BLOCK_COMMENTS_ON_FIRST_COLUMN, never_indent_block_comments_on_first_column ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_NEVER_INDENT_LINE_COMMENTS_ON_FIRST_COLUMN, never_indent_line_comments_on_first_column ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_PAGE_WIDTH, Integer.toString(page_width));
		options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, Integer.toString(tab_size));
		options.put(DefaultCodeFormatterConstants.FORMATTER_USE_TABS_ONLY_FOR_LEADING_INDENTATIONS, use_tabs_only_for_leading_indentations ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_EMPTY_LINES, indent_empty_lines ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE);
		options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, tab_char.toString());
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE, Integer.toString(indentation_size));
		options.put(DefaultCodeFormatterConstants.FORMATTER_CONTINUATION_INDENTATION, Integer.toString(continuation_indentation));
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
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON);
		if(null != current) {
			try {
				insert_space_before_semicolon = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_space_before_semicolon = false;
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
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AT_END_OF_FILE_IF_MISSING);
		if(null != current) {
			try {
				insert_new_line_at_end_of_file_if_missing = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				insert_new_line_at_end_of_file_if_missing = false;
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
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_LINE_SPLIT);
		if(null != current) {
			try {
				line_split = Integer.parseInt(current);
			} catch(Exception e) {
				line_split = 9999;
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
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_PAGE_WIDTH);
		if(null != current) {
			try {
				page_width = Integer.parseInt(current);
			} catch(Exception e) {
				page_width = 80;
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
		
		current = settings.get(DefaultCodeFormatterConstants.FORMATTER_INDENT_EMPTY_LINES);
		if(null != current) {
			try {
				indent_empty_lines = DefaultCodeFormatterConstants.TRUE.equals(current);
			} catch(Exception e) {
				indent_empty_lines = true;
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
		insert_space_before_semicolon = false;
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
		insert_space_before_closing_paren_in_function_invocation = false;
		insert_space_between_empty_parens_in_function_invocation = false;
		insert_new_line_at_end_of_file_if_missing = false;
		blank_lines_before_module = 0;
		blank_lines_after_module = 1;
		line_split = 9999;
		never_indent_block_comments_on_first_column = false;
		never_indent_line_comments_on_first_column = false;
		page_width = 80;
		tab_size = 4;
		use_tabs_only_for_leading_indentations = false;
		indent_empty_lines = true;
		tab_char = TabChar.TAB;
		indentation_size = 4;
		continuation_indentation = 2;
		number_of_empty_lines_to_preserve = 1;
	}
}
