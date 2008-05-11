/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Saff (saff@mit.edu) - bug 102632: [JUnit] Support for JUnit 4.
 *******************************************************************************/
package descent.internal.unittest.ui;

import org.eclipse.osgi.util.NLS;

public final class JUnitMessages extends NLS
{
	public static String CopyFailureList_action_label;
	public static String CopyFailureList_clipboard_busy;
	public static String CopyFailureList_problem;
	public static String CopyTrace_action_label;
	public static String CopyTraceAction_clipboard_busy;
	public static String CopyTraceAction_problem;
	public static String CounterPanel_label_errors;
	public static String CounterPanel_label_failures;
	public static String CounterPanel_label_runs;
	public static String CounterPanel_runcount;
	public static String CounterPanel_runcount_ignored;
	public static String DUnittestFinder_task_name;
	public static String ExpandAllAction_text;
	public static String ExpandAllAction_tooltip;
	public static String JUnitPreferencePage_description;
	public static String OpenEditorAction_action_label;
	public static String OpenEditorAction_error_cannotopen_message;
	public static String OpenEditorAction_error_cannotopen_title;
	public static String OpenEditorAction_error_dialog_message;
	public static String OpenEditorAction_error_dialog_title;
	public static String OpenEditorAction_message_cannotopen;
	public static String RerunAction_label_debug;
	public static String RerunAction_label_run;
    public static String RunningOneTest_internal_error;
	public static String ScrollLockAction_action_label;
	public static String ScrollLockAction_action_tooltip;
	public static String ShowNextFailureAction_label;
	public static String ShowNextFailureAction_tooltip;
	public static String ShowPreviousFailureAction_label;
	public static String ShowPreviousFailureAction_tooltip;
	public static String TestRunnerViewPart_activate_on_failure_only;
	public static String TestRunnerViewPart_cannotrerun_title;
	public static String TestRunnerViewPart_cannotrerurn_message;
	public static String TestRunnerViewPart_clear_history_label;
	public static String TestRunnerViewPart_configName;
	public static String TestRunnerViewPart_error_cannotrerun;
	public static String TestRunnerViewPart_hierarchical_layout;
	public static String TestRunnerViewPart_history;
	public static String TestRunnerViewPart_jobName;
	public static String TestRunnerViewPart_label_failure;
	public static String TestRunnerViewPart_Launching;
	public static String TestRunnerViewPart_layout_menu;
	public static String TestRunnerViewPart_max_remembered;
	public static String TestRunnerViewPart_message_finish;
	public static String TestRunnerViewPart_message_stopped;
	public static String TestRunnerViewPart_message_stopping;
	public static String TestRunnerViewPart_message_terminated;
	public static String TestRunnerViewPart_rerunaction_label;
	public static String TestRunnerViewPart_rerunaction_tooltip;
	public static String TestRunnerViewPart_rerunFailedFirstLaunchConfigName;
	public static String TestRunnerViewPart_rerunfailuresaction_label;
	public static String TestRunnerViewPart_rerunfailuresaction_tooltip;
	public static String TestRunnerViewPart_select_test_run;
	public static String TestRunnerViewPart_show_failures_only;
	public static String TestRunnerViewPart_stopaction_text;
	public static String TestRunnerViewPart_stopaction_tooltip;
	public static String TestRunnerViewPart_terminate_message;
	public static String TestRunnerViewPart_terminate_title;
	public static String TestRunnerViewPart_test_run_history;
	public static String TestRunnerViewPart_test_runs;
	public static String TestRunnerViewPart_testName_startTime;
	public static String TestRunnerViewPart_toggle_automatic_label;
	public static String TestRunnerViewPart_toggle_horizontal_label;
	public static String TestRunnerViewPart_toggle_vertical_label;
	public static String TestRunnerViewPart_wrapperJobName;
    public static String UnittestLaunchConfiguration_container_not_found;
    public static String UnittestLaunchConfiguration_could_not_connect;
    public static String UnittestLaunchConfiguration_could_not_delete_file;
    public static String UnittestLaunchConfiguration_error_crearing_file;
    public static String UnittestLaunchConfiguration_error_writing_file;
    public static String UnittestLaunchConfiguration_invalid_port;
    public static String UnittestLaunchConfiguration_no_open_port;
    public static String UnittestLaunchConfiguration_no_tests_found;
    public static String UnittestLaunchConfiguration_program_does_not_exist;
    public static String UnittestLaunchConfiguration_task_name;
    public static String UnittestLaunchConfigurationTab_default_package;
    public static String UnittestLaunchConfigurationTab_dialog_test_container_selection;
    public static String UnittestLaunchConfigurationTab_error_invalid_port;
    public static String UnittestLaunchConfigurationTab_error_no_port;
    public static String UnittestLaunchConfigurationTab_error_no_test_container;
    public static String UnittestLaunchConfigurationTab_error_port_number;
    public static String UnittestLaunchConfigurationTab_error_test_container_does_not_exist;
    public static String UnittestLaunchConfigurationTab_group_port;
    public static String UnittestLaunchConfigurationTab_group_test_selection;
    public static String UnittestLaunchConfigurationTab_label_automatically_choose_port;
	public static String UnittestLaunchConfigurationTab_label_browse;
    public static String UnittestLaunchConfigurationTab_label_include_subpackages;
    public static String UnittestLaunchConfigurationTab_label_port;
    public static String UnittestLaunchConfigurationTab_label_test_container;
    public static String UnittestLaunchConfigurationTab_label_test_container_selection;
    public static String UnittestLaunchConfigurationTab_label_use_specified_port;
	public static String UnittestLaunchConfigurationTab_tab_label;
	
	private static final String BUNDLE_NAME= "descent.internal.unittest.ui.JUnitMessages";//$NON-NLS-1$
	
	static {
        NLS.initializeMessages(BUNDLE_NAME, JUnitMessages.class);
    }

    private JUnitMessages() {
        // Do not instantiate
    }
}
