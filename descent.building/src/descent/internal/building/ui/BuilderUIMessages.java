package descent.internal.building.ui;

import org.eclipse.osgi.util.NLS;

public class BuilderUIMessages extends NLS
{
    private static final String BUNDLE_NAME = "descent.internal.building.ui.BuilderUIMessages"; //$NON-NLS-1$

    public static String AbstractBuilderTab_browse_label;
    
    public static String CompilerTab_column_option;
    public static String CompilerTab_column_value;
    public static String CompilerTab_compilers_preference_page_link;
    public static String CompilerTab_error_no_compiler;
    public static String CompilerTab_label_additional_compiler_args;
    public static String CompilerTab_label_additional_linker_args;
    public static String CompilerTab_tab_name;
    
    public static String GeneralTab_button_add;
    public static String GeneralTab_button_remove;
    public static String GeneralTab_error_element_does_not_exist;
    public static String GeneralTab_error_invalid_element;
    public static String GeneralTab_error_invalid_project_name;
    public static String GeneralTab_error_no_modules;
    public static String GeneralTab_error_no_output_path;
    public static String GeneralTab_error_no_project;
    public static String GeneralTab_error_not_a_d_project;
    public static String GeneralTab_error_project_does_not_exist;
    public static String GeneralTab_group_included_modules;
    public static String GeneralTab_group_output_target;
    public static String GeneralTab_included_modules_help;
    public static String GeneralTab_module_search_dialog_title;
    public static String GeneralTab_only_main_filter_label;
    public static String GeneralTab_option_executable;
    public static String GeneralTab_option_static_library;
    public static String GeneralTab_output_file_dialog_title;
    public static String GeneralTab_output_file_label;
    public static String GeneralTab_project_dialog_text;
    public static String GeneralTab_project_dialog_title;
    public static String GeneralTab_project_text_label;
    public static String GeneralTab_tab_name;
    public static String GeneralTab_target_type_label;
    
    public static String VersionTab_button_add;
    public static String VersionTab_button_remove;
    public static String VersionTab_debug;
    public static String VersionTab_debug_mode;
    public static String VersionTab_error_invalid_identifier;
    public static String VersionTab_error_predefined_version;
    public static String VersionTab_group_additional_versions;
    public static String VersionTab_identifiers;
    public static String VersionTab_level;
    public static String VersionTab_option_none;
    public static String VersionTab_option_selected_project;
    public static String VersionTab_option_workspace_project;
    public static String VersionTab_tab_name;
    public static String VersionTab_version;
    public static String VersionTab_version_source_label;
    
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, BuilderUIMessages.class);
    }

    private BuilderUIMessages()
    {
    }
}
