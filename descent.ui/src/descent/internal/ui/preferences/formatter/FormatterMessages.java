/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     istvan@benedek-home.de - 103706 [formatter] indent empty lines
 *     Aaron Luchko, aluchko@redhat.com - 105926 [Formatter] Exporting Unnamed profile fails silently
 *******************************************************************************/
package descent.internal.ui.preferences.formatter;

import org.eclipse.osgi.util.NLS;

/**
 * Helper class to get NLSed messages.
 */
final class FormatterMessages extends NLS {

	private static final String BUNDLE_NAME= FormatterMessages.class.getName(); 

	private FormatterMessages() {
		// Do not instantiate
	}
	
	// General messages
	public static String ModifyDialog_BuiltIn_Status;
	public static String ModifyDialog_Duplicate_Status;
	public static String ModifyDialog_EmptyName_Status;
	public static String ModifyDialog_Export_Button;
	public static String ModifyDialog_NewCreated_Status;
	public static String ModifyDialog_ProfileName_Label;
	public static String ModifyDialog_Shared_Status;
	public static String ProfileConfigurationBlock_load_profile_wrong_profile_message;
	public static String JavaPreview_formatter_exception;
	public static String AlreadyExistsDialog_message_profile_already_exists;
	public static String AlreadyExistsDialog_message_profile_name_empty;
	public static String AlreadyExistsDialog_dialog_title;
	public static String AlreadyExistsDialog_dialog_label;
	public static String AlreadyExistsDialog_rename_radio_button_desc;
	public static String AlreadyExistsDialog_overwrite_radio_button_desc;
	public static String CodingStyleConfigurationBlock_save_profile_dialog_title;
	public static String CodingStyleConfigurationBlock_save_profile_error_title;
	public static String CodingStyleConfigurationBlock_save_profile_error_message;
	public static String CodingStyleConfigurationBlock_load_profile_dialog_title;
	public static String CodingStyleConfigurationBlock_load_profile_error_title;
	public static String CodingStyleConfigurationBlock_load_profile_error_message;
	public static String CodingStyleConfigurationBlock_load_profile_error_too_new_title;
	public static String CodingStyleConfigurationBlock_load_profile_error_too_new_message;
	public static String CodingStyleConfigurationBlock_save_profile_overwrite_title;
	public static String CodingStyleConfigurationBlock_save_profile_overwrite_message;
	public static String CodingStyleConfigurationBlock_edit_button_desc;
	public static String CodingStyleConfigurationBlock_remove_button_desc;
	public static String CodingStyleConfigurationBlock_new_button_desc;
	public static String CodingStyleConfigurationBlock_load_button_desc;
	public static String CodingStyleConfigurationBlock_save_button_desc;
	public static String CodingStyleConfigurationBlock_preview_label_text;
	public static String CodingStyleConfigurationBlock_error_reading_xml_message;
	public static String CodingStyleConfigurationBlock_error_serializing_xml_message;
	public static String CodingStyleConfigurationBlock_delete_confirmation_title;
	public static String CodingStyleConfigurationBlock_delete_confirmation_question;
	public static String CreateProfileDialog_status_message_profile_with_this_name_already_exists;
	public static String CreateProfileDialog_status_message_profile_name_is_empty;
	public static String CreateProfileDialog_dialog_title;
	public static String CreateProfileDialog_profile_name_label_text;
	public static String CreateProfileDialog_base_profile_label_text;
	public static String CreateProfileDialog_open_edit_dialog_checkbox_text;
	public static String ModifyDialog_dialog_title;
	public static String ModifyDialog_apply_button;
	public static String ModifyDialogTabPage_preview_label_text;
	public static String ProfileManager_unmanaged_profile;
	public static String ProfileManager_unmanaged_profile_with_name;
	public static String ModifyDialogTabPage_error_msg_values_text_unassigned;
	public static String ModifyDialogTabPage_error_msg_values_items_text_unassigned;
	public static String ModifyDialogTabPage_NumberPreference_error_invalid_key;
	public static String ModifyDialogTabPage_NumberPreference_error_invalid_value;
	public static String ProfileManager_descent_defaults_profile_name;
	
	// Braces tab page
	public static String ModifyDialog_tabpage_braces_title;
	public static String BracesTabPage_preview_header;
	public static String BracesTabPage_position_same_line;
	public static String BracesTabPage_position_next_line;
	public static String BracesTabPage_position_next_line_indented;
	public static String BracesTabPage_group_brace_positions_title;
	public static String BracesTabPage_group_set_all_to;
	
	// Indentation tab page
	public static String ModifyDialog_tabpage_indentation_title;
	public static String IndentationTabPage_style_tab;
	public static String IndentationTabPage_style_space;
	public static String IndentationTabPage_style_mixed;
	public static String IndentationTabPage_preview_header;
	public static String IndentationTabPage_general_group_title;
	public static String IndentationTabPage_indent_group_title;
	
	// White space tab page
	public static String ModifyDialog_tabpage_white_space_title;
	public static String WhiteSpaceTabPage_insert_space;
	public static String WhiteSpaceTabPage_sort_by_d_element;
	public static String WhiteSpaceTabPage_sort_by_syntax_element;
	
	// White space options - D elements
	public static String WhiteSpaceOptions_statements;
	public static String WhiteSpaceOptions_for_statement;
	public static String WhiteSpaceOptions_foreach_statement;
	
	// White space options - syntax elements
	public static String WhiteSpaceOptions_before_semicolon;
	public static String WhiteSpaceOptions_after_semicolon;
	
	// Blank lines tab page
	public static String ModifyDialog_tabpage_blank_lines_title;
	public static String BlankLinesTabPage_preview_header;
	public static String BlankLinesTabPage_blank_lines_group_title;
	public static String BlankLinesTabPage_compilation_unit_group_title;
	
	// New Lines tab page
	public static String ModifyDialog_tabpage_new_lines_title;
	public static String NewLinesTabPage_preview_header;
	public static String NewLinesTabPage_short_syntax_group_title;
	public static String NewLinesTabPage_control_statements_title;
	public static String NewLinesTabPage_other_group_title;
	
	// Messages for various options (automatically generated)
	public static String BracesTabPage_brace_position_for_function_declaration;
	public static String BracesTabPage_brace_position_for_type_declaration;
	public static String BracesTabPage_brace_position_for_enum_declaration;
	public static String BracesTabPage_brace_position_for_template_declaration;
	public static String BracesTabPage_brace_position_for_conditional_declaration;
	public static String BracesTabPage_brace_position_for_conditional_statement;
	public static String BracesTabPage_brace_position_for_loop_statement;
	public static String BracesTabPage_brace_position_for_function_literal;
	public static String BracesTabPage_brace_position_for_anonymous_type;
	public static String BracesTabPage_brace_position_for_switch_statement;
	public static String BracesTabPage_brace_position_for_switch_case;
	public static String BracesTabPage_brace_position_for_try_catch_finally;
	public static String BracesTabPage_brace_position_for_modifiers;
	public static String BracesTabPage_brace_position_for_synchronized_statement;
	public static String BracesTabPage_brace_position_for_with_statement;
	public static String BracesTabPage_brace_position_for_scope_statement;
	public static String BracesTabPage_brace_position_for_other_blocks;
	public static String BlankLinesTabPage_blank_lines_before_module;
	public static String BlankLinesTabPage_blank_lines_after_module;
	public static String BlankLinesTabPage_number_of_empty_lines_to_preserve;
	public static String NewLinesTabPage_insert_new_line_before_else;
	public static String NewLinesTabPage_insert_new_line_before_catch;
	public static String NewLinesTabPage_insert_new_line_before_finally;
	public static String NewLinesTabPage_insert_new_line_before_while_in_do_statement;
	public static String NewLinesTabPage_keep_else_conditional_on_one_line;
	public static String NewLinesTabPage_insert_new_line_at_end_of_file_if_missing;
	public static String NewLinesTabPage_keep_simple_then_declaration_on_same_line;
	public static String NewLinesTabPage_keep_simple_else_declaration_on_same_line;
	public static String NewLinesTabPage_keep_simple_then_statement_on_same_line;
	public static String NewLinesTabPage_keep_simple_else_statement_on_same_line;
	public static String NewLinesTabPage_keep_simple_try_statement_on_same_line;
	public static String NewLinesTabPage_keep_simple_catch_statement_on_same_line;
	public static String NewLinesTabPage_keep_simple_finally_statement_on_same_line;
	public static String NewLinesTabPage_keep_simple_loop_statement_on_same_line;
	public static String NewLinesTabPage_keep_simple_synchronized_statement_on_same_line;
	public static String NewLinesTabPage_keep_simple_with_statement_on_same_line;
	public static String NewLinesTabPage_keep_functions_with_no_statement_in_one_line;
	public static String NewLinesTabPage_keep_functions_with_one_statement_in_one_line;
	public static String IndentationTabPage_indentation_size;
	public static String IndentationTabPage_indent_empty_lines;
	public static String IndentationTabPage_indent_body_declarations_compare_to_type_header;
	public static String IndentationTabPage_indent_body_declarations_compare_to_template_header;
	public static String IndentationTabPage_indent_body_declarations_compare_to_modifier_header;
	public static String IndentationTabPage_indent_statements_compare_to_function_header;
	public static String IndentationTabPage_indent_in_out_body_compare_to_function_header;
	public static String IndentationTabPage_indent_statements_compare_to_function_in_header;
	public static String IndentationTabPage_indent_statements_compare_to_function_out_header;
	public static String IndentationTabPage_indent_statements_compare_to_function_body_header;
	public static String IndentationTabPage_indent_enum_members_compare_to_enum_header;
	public static String IndentationTabPage_tab_char;
	public static String IndentationTabPage_tab_size;

	static {
		NLS.initializeMessages(BUNDLE_NAME, FormatterMessages.class);
	}
}
