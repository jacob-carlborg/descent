/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.ui.search;

import org.eclipse.osgi.util.NLS;

public final class SearchMessages extends NLS {

	private static final String BUNDLE_NAME= "descent.internal.ui.search.SearchMessages";//$NON-NLS-1$

	private SearchMessages() {
		// Do not instantiate
	}

	public static String JavaSearchResultPage_preferences_label;
	public static String JavaSearchScopeFactory_undefined_projects;
	public static String JavaSearchScopeFactory_undefined_selection;
	public static String JavaSearchScopeFactory_undefined_workingsets;
	public static String SearchLabelProvider_exact_singular;
	public static String SearchLabelProvider_exact_noCount;
	public static String SearchLabelProvider_exact_and_potential_plural;
	public static String SearchLabelProvider_potential_singular;
	public static String SearchLabelProvider_potential_noCount;
	public static String SearchLabelProvider_potential_plural;
	public static String SearchLabelProvider_exact_plural;
	public static String group_search;
	public static String group_declarations;
	public static String group_references;
	public static String group_readReferences;
	public static String group_writeReferences;
	public static String group_implementors;
	public static String group_occurrences;
	public static String group_occurrences_quickMenu_noEntriesAvailable;
	public static String Search_Error_search_title;
	public static String Search_Error_search_message;
	public static String Search_Error_search_notsuccessful_message;
	public static String Search_Error_javaElementAccess_title;
	public static String Search_Error_javaElementAccess_message;
	public static String Search_Error_search_notsuccessful_title;
	public static String Search_Error_openEditor_title;
	public static String Search_Error_openEditor_message;
	public static String Search_Error_codeResolve;
	public static String SearchElementSelectionDialog_title;
	public static String SearchElementSelectionDialog_message;
	public static String SearchPage_searchFor_label;
	public static String SearchPage_searchFor_type;
	public static String SearchPage_searchFor_method;
	public static String SearchPage_searchFor_field;
	public static String SearchPage_searchFor_package;
	public static String SearchPage_searchFor_constructor;
	public static String SearchPage_limitTo_label;
	public static String SearchPage_limitTo_declarations;
	public static String SearchPage_limitTo_implementors;
	public static String SearchPage_limitTo_references;
	public static String SearchPage_limitTo_allOccurrences;
	public static String SearchPage_limitTo_readReferences;
	public static String SearchPage_limitTo_writeReferences;
	public static String SearchPage_expression_label;
	public static String SearchPage_expression_caseSensitive;
	public static String SearchPage_searchJRE_label;
	public static String SearchUtil_workingSetConcatenation;
	public static String Search_FindDeclarationAction_label;
	public static String Search_FindDeclarationAction_tooltip;
	public static String Search_FindDeclarationsInProjectAction_label;
	public static String Search_FindDeclarationsInProjectAction_tooltip;
	public static String Search_FindDeclarationsInWorkingSetAction_label;
	public static String Search_FindDeclarationsInWorkingSetAction_tooltip;
	public static String Search_FindHierarchyDeclarationsAction_label;
	public static String Search_FindHierarchyDeclarationsAction_tooltip;
	public static String Search_FindImplementorsAction_label;
	public static String Search_FindImplementorsAction_tooltip;
	public static String Search_FindImplementorsInProjectAction_label;
	public static String Search_FindImplementorsInProjectAction_tooltip;
	public static String Search_FindImplementorsInWorkingSetAction_label;
	public static String Search_FindImplementorsInWorkingSetAction_tooltip;
	public static String Search_FindReferencesAction_label;
	public static String Search_FindReferencesAction_tooltip;
	public static String Search_FindReferencesAction_BinPrimConstWarnDialog_title;
	public static String Search_FindReferencesAction_BinPrimConstWarnDialog_message;
	public static String Search_FindReferencesInProjectAction_label;
	public static String Search_FindReferencesInProjectAction_tooltip;
	public static String Search_FindReferencesInWorkingSetAction_label;
	public static String Search_FindReferencesInWorkingSetAction_tooltip;
	public static String Search_FindHierarchyReferencesAction_label;
	public static String Search_FindHierarchyReferencesAction_tooltip;
	public static String Search_FindReadReferencesAction_label;
	public static String Search_FindReadReferencesAction_tooltip;
	public static String Search_FindReadReferencesInProjectAction_label;
	public static String Search_FindReadReferencesInProjectAction_tooltip;
	public static String Search_FindReadReferencesInWorkingSetAction_label;
	public static String Search_FindReadReferencesInWorkingSetAction_tooltip;
	public static String Search_FindReadReferencesInHierarchyAction_label;
	public static String Search_FindReadReferencesInHierarchyAction_tooltip;
	public static String Search_FindWriteReferencesAction_label;
	public static String Search_FindWriteReferencesAction_tooltip;
	public static String Search_FindWriteReferencesInProjectAction_label;
	public static String Search_FindWriteReferencesInProjectAction_tooltip;
	public static String Search_FindWriteReferencesInWorkingSetAction_label;
	public static String Search_FindWriteReferencesInWorkingSetAction_tooltip;
	public static String Search_FindWriteReferencesInHierarchyAction_label;
	public static String Search_FindWriteReferencesInHierarchyAction_tooltip;
	public static String Search_FindOccurrencesInFile_shortLabel;
	public static String Search_FindOccurrencesInFile_label;
	public static String Search_FindOccurrencesInFile_tooltip;
	public static String FindOccurrencesEngine_noSource_text;
	public static String FindOccurrencesEngine_cannotParse_text;
	public static String OccurrencesFinder_no_element;
	public static String OccurrencesFinder_no_binding;
	public static String OccurrencesFinder_searchfor;
	public static String OccurrencesFinder_label_singular;
	public static String OccurrencesFinder_label_plural;
	public static String ExceptionOccurrencesFinder_no_exception;
	public static String ExceptionOccurrencesFinder_searchfor;
	public static String ExceptionOccurrencesFinder_label_singular;
	public static String ExceptionOccurrencesFinder_label_plural;
	public static String ImplementOccurrencesFinder_invalidTarget;
	public static String ImplementOccurrencesFinder_searchfor;
	public static String ImplementOccurrencesFinder_label_singular;
	public static String ImplementOccurrencesFinder_label_plural;
	public static String JavaSearchOperation_singularDeclarationsPostfix;
	public static String JavaSearchOperation_singularReferencesPostfix;
	public static String JavaSearchOperation_singularReadReferencesPostfix;
	public static String JavaSearchOperation_singularWriteReferencesPostfix;
	public static String JavaSearchOperation_singularImplementorsPostfix;
	public static String JavaSearchOperation_singularOccurrencesPostfix;
	public static String JavaSearchOperation_pluralDeclarationsPostfix;
	public static String JavaSearchOperation_pluralReferencesPostfix;
	public static String JavaSearchOperation_pluralReadReferencesPostfix;
	public static String JavaSearchOperation_pluralWriteReferencesPostfix;
	public static String JavaSearchOperation_pluralImplementorsPostfix;
	public static String JavaSearchOperation_pluralOccurrencesPostfix;
	public static String JavaElementAction_typeSelectionDialog_title;
	public static String JavaElementAction_typeSelectionDialog_message;
	public static String JavaElementAction_error_open_message;
	public static String JavaElementAction_operationUnavailable_title;
	public static String JavaElementAction_operationUnavailable_generic;
	public static String JavaElementAction_operationUnavailable_field;
	public static String JavaElementAction_operationUnavailable_interface;
	public static String WorkspaceScope;
	public static String WorkspaceScopeNoJRE;
	public static String SingleWorkingSetScope;
	public static String SingleWorkingSetScopeNoJRE;
	public static String DoubleWorkingSetScope;
	public static String DoubleWorkingSetScopeNoJRE;
	public static String WorkingSetsScope;
	public static String WorkingSetsScopeNoJRE;
	
	public static String SelectionScope;
	public static String SelectionScopeNoJRE;
	public static String SingleSelectionScope;
	public static String SingleSelectionScopeNoJRE;
	public static String DoubleSelectionScope;
	public static String DoubleSelectionScopeNoJRE;
	public static String EnclosingProjectsScope;
	public static String EnclosingProjectsScopeNoJRE;
	public static String EnclosingProjectsScope2;
	public static String EnclosingProjectsScope2NoJRE;
	public static String EnclosingProjectScope;
	public static String EnclosingProjectScopeNoJRE;
	public static String ProjectScope;
	public static String ProjectScopeNoJRE;
	public static String HierarchyScope;
	public static String JavaSearchResultPage_sortByName;
	public static String JavaSearchResultPage_sortByPath;
	public static String JavaSearchResultPage_open_editor_error_title;
	public static String JavaSearchResultPage_open_editor_error_message;
	public static String JavaSearchResultPage_sortByParentName;
	public static String JavaSearchResultPage_filtered_message;
	public static String JavaSearchResultPage_sortBylabel;
	public static String JavaSearchResultPage_error_marker;
	public static String JavaSearchResultPage_groupby_project;
	public static String JavaSearchResultPage_groupby_project_tooltip;
	public static String JavaSearchResultPage_groupby_package;
	public static String JavaSearchResultPage_groupby_package_tooltip;
	public static String JavaSearchResultPage_filteredWithCount_message;
	public static String JavaSearchResultPage_groupby_file;
	public static String JavaSearchResultPage_groupby_file_tooltip;
	public static String JavaSearchResultPage_groupby_type;
	public static String JavaSearchResultPage_groupby_type_tooltip;
	public static String JavaSearchQuery_task_label;
	public static String JavaSearchQuery_label;
	public static String JavaSearchQuery_error_unsupported_pattern;
	public static String JavaSearchQuery_status_ok_message;
	public static String JavaSearchQuery_error_participant_estimate;
	public static String JavaSearchQuery_error_participant_search;
	public static String SearchParticipant_error_noID;
	public static String SearchParticipant_error_noNature;
	public static String SearchParticipant_error_noClass;
	public static String SearchParticipant_error_classCast;
	public static String MatchFilter_ImportFilter_name;
	public static String MatchFilter_ImportFilter_actionLabel;
	public static String MatchFilter_ImportFilter_description;
	public static String MatchFilter_WriteFilter_name;
	public static String MatchFilter_WriteFilter_actionLabel;
	public static String MatchFilter_WriteFilter_description;
	public static String MatchFilter_ReadFilter_name;
	public static String MatchFilter_ReadFilter_actionLabel;
	public static String MatchFilter_ReadFilter_description;
	public static String MatchFilter_JavadocFilter_name;
	public static String MatchFilter_JavadocFilter_actionLabel;
	public static String MatchFilter_JavadocFilter_description;
	public static String MatchFilter_ErasureFilter_name;
	public static String MatchFilter_ErasureFilter_actionLabel;
	public static String MatchFilter_ErasureFilter_description;
	public static String MatchFilter_InexactFilter_name;
	public static String MatchFilter_InexactFilter_actionLabel;
	public static String MatchFilter_InexactFilter_description;
	public static String MethodExitsFinder_no_return_type_selected;
	public static String BreakContinueTargetFinder_cannot_highlight;
	public static String BreakContinueTargetFinder_no_break_or_continue_selected;
	public static String TextSearchLabelProvider_matchCountFormat;
	public static String FiltersDialog_title;
	public static String FiltersDialog_filters_label;
	public static String FiltersDialog_description_label;
	public static String FiltersDialog_limit_label;
	public static String FiltersDialog_limit_error;
	public static String FiltersDialogAction_label;

	static {
		NLS.initializeMessages(BUNDLE_NAME, SearchMessages.class);
	}

	public static String JavaSearchQuery_error_element_does_not_exist;
	public static String MatchFilter_PotentialFilter_name;
	public static String MatchFilter_PotentialFilter_actionLabel;
	public static String MatchFilter_PotentialFilter_description;
}
