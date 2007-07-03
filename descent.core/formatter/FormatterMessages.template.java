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
	public static String WhiteSpaceOptions_declarations;
	public static String WhiteSpaceOptions_statements;
	public static String WhiteSpaceOptions_for_statement;
	public static String WhiteSpaceOptions_foreach_statement;
	public static String WhiteSpaceOptions_variable_declaration;
	public static String WhiteSpaceOptions_function_declaration;
	public static String WhiteSpaceOptions_function_invocation;
	public static String WhiteSpaceOptions_function_arguments;
	
	// White space options - syntax elements
	public static String WhiteSpaceOptions_before_semicolon;
	public static String WhiteSpaceOptions_after_semicolon;
	public static String WhiteSpaceOptions_before_comma;
	public static String WhiteSpaceOptions_after_comma;
	public static String WhiteSpaceOptions_before_opening_paren;
	public static String WhiteSpaceOptions_after_opening_paren;
	public static String WhiteSpaceOptions_before_closing_paren;
	public static String WhiteSpaceOptions_between_empty_parens;
	
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
	/* EVAL-FOR-EACH
	 * 
	 * if($$_{'tab'})
	 * {
	 *     print DST "\tpublic static String " . $$_{'tab'} . "_" . $$_{'optName'} . ";\n";
	 * }
	 *
	 */

	static {
		NLS.initializeMessages(BUNDLE_NAME, FormatterMessages.class);
	}
}
