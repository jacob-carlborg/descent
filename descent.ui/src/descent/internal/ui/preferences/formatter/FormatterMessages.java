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
	public static String ProfileManager_java_defaults_profile_name;
	public static String ProfileManager_c_sharp_defaults_profile_name;
	
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
	public static String WhiteSpaceOptions_declarations;
	public static String WhiteSpaceOptions_statements;
	public static String WhiteSpaceOptions_expressions;
	public static String WhiteSpaceOptions_for_statement;
	public static String WhiteSpaceOptions_foreach_statement;
	public static String WhiteSpaceOptions_variable_declaration;
	public static String WhiteSpaceOptions_function_declaration;
	public static String WhiteSpaceOptions_function_invocation;
	public static String WhiteSpaceOptions_function_arguments;
	public static String WhiteSpaceOptions_function_invocation_arguments;
	public static String WhiteSpaceOptions_catch_statement;
	public static String WhiteSpaceOptions_while_statement;
	public static String WhiteSpaceOptions_synchronized_statement;
	public static String WhiteSpaceOptions_switch_statement;
	public static String WhiteSpaceOptions_align_declaration;
	public static String WhiteSpaceOptions_aggregate_declaration;
	public static String WhiteSpaceOptions_assert_statement;
	public static String WhiteSpaceOptions_version_debug;
	public static String WhiteSpaceOptions_mixin;
	public static String WhiteSpaceOptions_pragma;
	public static String WhiteSpaceOptions_scope_statement;
	public static String WhiteSpaceOptions_with_statement;
	public static String WhiteSpaceOptions_typeof;
	public static String WhiteSpaceOptions_typeid;
	public static String WhiteSpaceOptions_between_template_and_arg_parens;
	public static String WhiteSpaceOptions_function_delegate_type;
	public static String WhiteSpaceOptions_anonymous_function;
	public static String WhiteSpaceOptions_anonymous_class;
	public static String WhiteSpaceOptions_c_style_function_pointer;
	public static String WhiteSpaceOptions_new_params;
	public static String WhiteSpaceOptions_extern_declarations;
	public static String WhiteSpaceOptions_file_import_declarations;
	public static String WhiteSpaceOptions_if_statements;
	public static String WhiteSpaceOptions_is_expressions;
	public static String WhiteSpaceOptions_casts;
	public static String WhiteSpaceOptions_template_declaration;
	public static String WhiteSpaceOptions_function_argument_list;
	public static String WhiteSpaceOptions_aggregate_template_args;
	public static String WhiteSpaceOptions_function_declaration_parameter_list;
	public static String WhiteSpaceOptions_function_invocation_argument_list;
	public static String WhiteSpaceOptions_name_and_arg_parens_in_c_style_fp;
	public static String WhiteSpaceOptions_template_and_function_params_in_function_declaration;
	public static String WhiteSpaceOptions_template_and_function_args_in_function_invocation;
	public static String WhiteSpaceOptions_successive_opcalls;
	public static String WhiteSpaceOptions_function_invocation_args;
	public static String WhiteSpaceOptions_function_decl_params;
	public static String WhiteSpaceOptions_function_decl_params_ex;
	public static String WhiteSpaceOptions_function_template_params;
	public static String WhiteSpaceOptions_function_template_params_ex;
	public static String WhiteSpaceOptions_out_declaration;
	public static String WhiteSpaceOptions_aggregate_template_params;
	public static String WhiteSpaceOptions_aggregate_template_params_ex;
	public static String WhiteSpaceOptions_type_dot_identifier_expression;
	public static String WhiteSpaceOptions_parenthesized_expressions;
	public static String WhiteSpaceOptions_template_invocation;
	public static String WhiteSpaceOptions_type_dot_identifier_expression_parens;
	public static String WhiteSpaceOptions_array_literal;
	public static String WhiteSpaceOptions_alias_typedef_declaration;
	public static String WhiteSpaceOptions_struct_initalizer;
	public static String WhiteSpaceOptions_array_slice;
	public static String WhiteSpaceOptions_import_declaration;
	public static String WhiteSpaceOptions_base_class_lists;
	public static String WhiteSpaceOptions_multiple_imports;
	public static String WhiteSpaceOptions_selective_imports;
	public static String WhiteSpaceOptions_operators;
	public static String WhiteSpaceOptions_assignment_operator;
	public static String WhiteSpaceOptions_prefix_operator;
	public static String WhiteSpaceOptions_binary_operator;
	public static String WhiteSpaceOptions_postfix_operator;
	public static String WhiteSpaceOptions_qualified_names;
	public static String WhiteSpaceOptions_function_varargs;
	public static String WhiteSpaceOptions_tuple_parameters;
	public static String WhiteSpaceOptions_dynamic_arrays;
	public static String WhiteSpaceOptions_arrays;
	public static String WhiteSpaceOptions_conditional_expression;
	public static String WhiteSpaceOptions_case_default_label;
	public static String WhiteSpaceOptions_labels;
	public static String WhiteSpaceOptions_modifier_blocks;
	public static String WhiteSpaceOptions_template_parameter_sepcialization;
	public static String WhiteSpaceOptions_multidimensional_arrays;
	
	// White space options - syntax elements
	public static String WhiteSpaceOptions_before_semicolon;
	public static String WhiteSpaceOptions_after_semicolon;
	public static String WhiteSpaceOptions_before_comma;
	public static String WhiteSpaceOptions_after_comma;
	public static String WhiteSpaceOptions_before_opening_paren;
	public static String WhiteSpaceOptions_after_opening_paren;
	public static String WhiteSpaceOptions_before_closing_paren;
	public static String WhiteSpaceOptions_after_closing_paren;
	public static String WhiteSpaceOptions_between_empty_parens;
	public static String WhiteSpaceOptions_between_adjacent_parens;
	public static String WhiteSpaceOptions_between_name_and_arg_parens;
	public static String WhiteSpaceOptions_between_template_args_and_function_args;
	public static String WhiteSpaceOptions_between_succesive_opcalls;
	public static String WhiteSpaceOptions_before_paren_in_argument_list;
	public static String WhiteSpaceOptions_before_opening_paren_in_parameter_list;
	public static String WhiteSpaceOptions_before_comma_in_multiple_imports;
	public static String WhiteSpaceOptions_before_comma_in_selective_imports;
	public static String WhiteSpaceOptions_after_comma_in_multiple_imports;
	public static String WhiteSpaceOptions_after_comma_in_selective_imports;
	public static String WhiteSpaceOptions_before_comma_in_base_class_lists;
	public static String WhiteSpaceOptions_after_comma_in_base_class_lists;
	public static String WhiteSpaceOptions_trailing_comma_in_array_literal;
	public static String WhiteSpaceOptions_before_trailing_comma;
	public static String WhiteSpaceOptions_after_trailing_comma;
	public static String WhiteSpaceOptions_before_operator;
	public static String WhiteSpaceOptions_after_operator;
	public static String WhiteSpaceOptions_before_assignment_operator;
	public static String WhiteSpaceOptions_before_prefix_operator;
	public static String WhiteSpaceOptions_before_binary_operator;
	public static String WhiteSpaceOptions_before_postfix_operator;
	public static String WhiteSpaceOptions_after_assignment_operator;
	public static String WhiteSpaceOptions_after_prefix_operator;
	public static String WhiteSpaceOptions_after_binary_operator;
	public static String WhiteSpaceOptions_after_postfix_operator;
	public static String WhiteSpaceOptions_before_exclamation_point;
	public static String WhiteSpaceOptions_after_exclamation_point;
	public static String WhiteSpaceOptions_before_dot;
	public static String WhiteSpaceOptions_after_dot;
	public static String WhiteSpaceOptions_before_slice_operator;
	public static String WhiteSpaceOptions_after_slice_operator;
	public static String WhiteSpaceOptions_before_elipsis_in_tuple_parameters;
	public static String WhiteSpaceOptions_after_elipsis_in_tuple_parameters;
	public static String WhiteSpaceOptions_before_elipsis_in_varargs;
	public static String WhiteSpaceOptions_after_elipsis_in_varargs;
	public static String WhiteSpaceOptions_after_elipsis;
	public static String WhiteSpaceOptions_before_elipsis;
	public static String WhiteSpaceOptions_between_empty_brackets;
	public static String WhiteSpaceOptions_before_colon;
	public static String WhiteSpaceOptions_after_colon;
	public static String WhiteSpaceOptions_before_colon_in_case_default_labels;
	public static String WhiteSpaceOptions_after_colon_in_case_default_labels;
	public static String WhiteSpaceOptions_before_colon_in_selective_imports;
	public static String WhiteSpaceOptions_after_colon_in_selective_imports;
	public static String WhiteSpaceOptions_before_colon_in_base_class_lists;
	public static String WhiteSpaceOptions_after_colon_in_base_class_lists;
	public static String WhiteSpaceOptions_before_colon_in_labeled_statements;
	public static String WhiteSpaceOptions_after_colon_in_labeled_statements;
	public static String WhiteSpaceOptions_before_colon_in_parameter_specialization;
	public static String WhiteSpaceOptions_after_colon_in_parameter_specialization;
	public static String WhiteSpaceOptions_before_question_mark;
	public static String WhiteSpaceOptions_after_question_mark;
	public static String WhiteSpaceOptions_between_adjacent_brackets;
	public static String WhiteSpaceOptions_before_opening_bracket;
	public static String WhiteSpaceOptions_after_opening_bracket;
	public static String WhiteSpaceOptions_before_closing_bracket;
	public static String WhiteSpaceOptions_before_opening_brace;
	public static String WhiteSpaceOptions_after_opening_brace;
	public static String WhiteSpaceOptions_before_closing_brace;
	public static String whiteSpaceOptions_after_closing_brace;
	
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
	
	// Short syntax tab page
	public static String ModifyDialog_tabpage_short_syntax_title;
	public static String ShortSyntaxTabPage_preview_header;
	public static String ShortSyntaxTabPage_short_syntax_group_title;
	
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
	public static String BracesTabPage_brace_position_for_pragmas;
	public static String BlankLinesTabPage_blank_lines_before_module;
	public static String BlankLinesTabPage_blank_lines_after_module;
	public static String BlankLinesTabPage_number_of_empty_lines_to_preserve;
	public static String NewLinesTabPage_insert_new_line_before_else;
	public static String NewLinesTabPage_insert_new_line_before_catch;
	public static String NewLinesTabPage_insert_new_line_before_finally;
	public static String NewLinesTabPage_insert_new_line_before_while_in_do_statement;
	public static String NewLinesTabPage_insert_new_line_after_case_or_default_statement;
	public static String NewLinesTabPage_insert_new_line_after_label;
	public static String NewLinesTabPage_keep_else_conditional_on_one_line;
	public static String NewLinesTabPage_insert_new_line_at_end_of_file_if_missing;
	public static String ShortSyntaxTabPage_keep_simple_then_declaration_on_same_line;
	public static String ShortSyntaxTabPage_keep_simple_else_declaration_on_same_line;
	public static String ShortSyntaxTabPage_keep_simple_then_statement_on_same_line;
	public static String ShortSyntaxTabPage_keep_simple_else_statement_on_same_line;
	public static String ShortSyntaxTabPage_keep_simple_try_statement_on_same_line;
	public static String ShortSyntaxTabPage_keep_simple_catch_statement_on_same_line;
	public static String ShortSyntaxTabPage_keep_simple_finally_statement_on_same_line;
	public static String ShortSyntaxTabPage_keep_simple_loop_statement_on_same_line;
	public static String ShortSyntaxTabPage_keep_simple_synchronized_statement_on_same_line;
	public static String ShortSyntaxTabPage_keep_simple_with_statement_on_same_line;
	public static String ShortSyntaxTabPage_keep_functions_with_no_statement_in_one_line;
	public static String ShortSyntaxTabPage_keep_functions_with_one_statement_in_one_line;
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
	public static String IndentationTabPage_indent_cases_compare_to_switch;
	public static String IndentationTabPage_indent_break_compare_to_switch;
	public static String IndentationTabPage_indent_statements_compare_to_case;
	public static String IndentationTabPage_tab_char;
	public static String IndentationTabPage_tab_size;

	static {
		NLS.initializeMessages(BUNDLE_NAME, FormatterMessages.class);
	}
}
