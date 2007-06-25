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
	
	// Profile management
	public static String ModifyDialog_BuiltIn_Status;
	public static String ModifyDialog_Duplicate_Status;
	public static String ModifyDialog_EmptyName_Status;
	public static String ModifyDialog_Export_Button;
	public static String ModifyDialog_NewCreated_Status;
	public static String ModifyDialog_ProfileName_Label;
	public static String ModifyDialog_Shared_Status;
	public static String ProfileConfigurationBlock_load_profile_wrong_profile_message;
	public static String ModifyDialog_tabpage_braces_title;
	public static String ModifyDialog_tabpage_indentation_title;
	public static String ModifyDialog_tabpage_whitespace_title;
	public static String ModifyDialog_tabpage_blank_lines_title;
	public static String ModifyDialog_tabpage_new_lines_title;
	public static String ModifyDialog_tabpage_control_statements_title;
	public static String ModifyDialog_tabpage_line_wrapping_title;
	public static String ModifyDialog_tabpage_comments_title;
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
	public static String BracesTabPage_preview_header;
	public static String BracesTabPage_position_same_line;
	public static String BracesTabPage_position_next_line;
	public static String BracesTabPage_position_next_line_indented;
	public static String BracesTabPage_group_brace_positions_title;
	public static String BracesTabPage_group_set_all_to;
	
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

	static {
		NLS.initializeMessages(BUNDLE_NAME, FormatterMessages.class);
	}
}
