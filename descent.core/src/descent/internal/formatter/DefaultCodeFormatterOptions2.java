/*
 * This file has been automatically generated.
 */
package descent.internal.formatter;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants2;
import descent.core.JavaCore;
import descent.internal.formatter.align.Alignment2;

public class DefaultCodeFormatterOptions2
{
	public static final int TAB = 1;
	public static final int SPACE = 2;
	public static final int MIXED = 4;
	
	public static DefaultCodeFormatterOptions2 getDefaultSettings() {
		DefaultCodeFormatterOptions2 options = new DefaultCodeFormatterOptions2();
		options.setDefaultSettings();
		return options;
	}
	
	public static DefaultCodeFormatterOptions2 getEclipseDefaultSettings() {
		DefaultCodeFormatterOptions2 options = new DefaultCodeFormatterOptions2();
		options.setDefaultSettings();
		return options;
	}

	public static DefaultCodeFormatterOptions2 getJavaConventionsSettings() {
		DefaultCodeFormatterOptions2 options = new DefaultCodeFormatterOptions2();
		options.setDefaultSettings();
		return options;
	}
	
	// User formatting options
	public int blank_lines_before_module;
	public int blank_lines_after_module;
	public boolean insert_space_before_semicolon;
	public boolean insert_space_before_comma_in_multiple_field_declarations;
	public boolean insert_space_after_comma_in_multiple_field_declarations;
	public boolean insert_space_between_empty_parens_in_method_declaration;
	public boolean insert_space_after_opening_paren_in_function_declaration;
	public boolean insert_space_before_closing_paren_in_function_declaration;
	public boolean insert_space_before_opening_paren_in_function_declaration;
	public int page_width;
	public int tab_size;
	public boolean use_tabs_only_for_leading_indentations;
	public boolean indent_empty_lines;
	public int tab_char;
	public int indentation_size;
	public int continuation_indentation;
	public int number_of_empty_lines_to_preserve;
	public boolean insert_new_line_at_end_of_file_if_missing;
	
	// Set by the caller
	public String line_separator;
    public int initial_indentation_level;
	
	private DefaultCodeFormatterOptions2() {
		// cannot be instantiated
	}

	public DefaultCodeFormatterOptions2(Map settings) {
		setDefaultSettings();
		if (settings == null) return;
		set(settings);
	}

	private String getAlignment2(int alignment) {
		return Integer.toString(alignment);
	}

	public Map getMap() {
		Map options = new HashMap();
		
		 
		return options;
	}

	public void set(Map settings) {
		// TODO create foreach here
	}

	public void setDefaultSettings() {
		blank_lines_after_module = 1;
		insert_space_before_semicolon = false;
		insert_space_before_comma_in_multiple_field_declarations = false;
		insert_space_after_comma_in_multiple_field_declarations = true;
		insert_space_between_empty_parens_in_method_declaration = false;
		insert_space_after_opening_paren_in_function_declaration = false;
		insert_space_before_closing_paren_in_function_declaration = false;
		insert_space_before_opening_paren_in_function_declaration = false;
		page_width = 80;
		tab_size = 4;
		use_tabs_only_for_leading_indentations = false;
		indent_empty_lines = true;
		tab_char = TAB;
		indentation_size = 4;
		continuation_indentation = 2;
		number_of_empty_lines_to_preserve = 1;
		insert_new_line_at_end_of_file_if_missing = true;
	}
}
