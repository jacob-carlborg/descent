/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.ui.javaeditor;

import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Helper class to get NLSed messages.
 */
final class JavaEditorMessages extends NLS {

	private static final String BUNDLE_FOR_CONSTRUCTED_KEYS= "descent.internal.ui.javaeditor.ConstructedJavaEditorMessages";//$NON-NLS-1$
	private static ResourceBundle fgBundleForConstructedKeys= ResourceBundle.getBundle(BUNDLE_FOR_CONSTRUCTED_KEYS);

	/**
	 * Returns the message bundle which contains constructed keys.
	 *
	 * @since 3.1
	 * @return the message bundle
	 */
	public static ResourceBundle getBundleForConstructedKeys() {
		return fgBundleForConstructedKeys;
	}

	private static final String BUNDLE_NAME= JavaEditorMessages.class.getName();


	private JavaEditorMessages() {
		// Do not instantiate
	}

	public static String AddImportOnSelection_label;
	public static String AddImportOnSelection_tooltip;
	public static String AddImportOnSelection_description;
	public static String AddImportOnSelection_error_title;
	public static String AddImportOnSelection_dialog_title;
	public static String AddImportOnSelection_dialog_message;
	public static String ClassFileEditor_error_classfile_not_on_classpath;
	public static String ClassFileEditor_error_invalid_input_message;
	public static String ClassFileEditor_error_title;
	public static String ClassFileEditor_error_message;
	public static String ClassFileMarkerAnnotationModel_error_isAcceptable;
	public static String ClassFileMarkerAnnotationModel_error_isAffected;
	public static String ClassFileMarkerAnnotationModel_error_resourceChanged;
	public static String CompilationUnitEditor_error_saving_message1;
	public static String CompilationUnitEditor_error_saving_message2;
	public static String CompilationUnitEditor_error_saving_title1;
	public static String CompilationUnitEditor_error_saving_title2;
	public static String CompilationUnitEditor_warning_save_delete;
	public static String JavaOutlinePage_Sort_label;
	public static String JavaOutlinePage_Sort_tooltip;
	public static String JavaOutlinePage_Sort_description;
	public static String JavaOutlinePage_GoIntoTopLevelType_label;
	public static String JavaOutlinePage_GoIntoTopLevelType_tooltip;
	public static String JavaOutlinePage_GoIntoTopLevelType_description;
	public static String JavaOutlinePage_error_NoTopLevelType;
	public static String ToggleComment_error_title;
	public static String ToggleComment_error_message;
	public static String ContentAssistProposal_label;
	public static String ShowJavaDoc_label;
	public static String Editor_FoldingMenu_name;
	public static String CompilationUnitDocumentProvider_saveAsTargetOpenInEditor;
	public static String ClassFileDocumentProvider_error_createElementInfo;
	public static String StructureSelect_error_title;
	public static String StructureSelect_error_message;
	public static String StructureSelectNext_label;
	public static String StructureSelectNext_tooltip;
	public static String StructureSelectNext_description;
	public static String StructureSelectPrevious_label;
	public static String StructureSelectPrevious_tooltip;
	public static String StructureSelectPrevious_description;
	public static String StructureSelectEnclosing_label;
	public static String StructureSelectEnclosing_tooltip;
	public static String StructureSelectEnclosing_description;
	public static String StructureSelectHistory_label;
	public static String StructureSelectHistory_tooltip;
	public static String StructureSelectHistory_description;
	public static String ExpandSelectionMenu_label;
	public static String GotoNextMember_label;
	public static String GotoPreviousMember_label;
	public static String GotoMatchingBracket_label;
	public static String GotoMatchingBracket_error_invalidSelection;
	public static String GotoMatchingBracket_error_noMatchingBracket;
	public static String GotoMatchingBracket_error_bracketOutsideSelectedElement;
	public static String SourceAttachmentForm_title;
	public static String SourceAttachmentForm_heading;
	public static String SourceAttachmentForm_message_noSource;
	public static String SourceAttachmentForm_message_noSourceAttachment;
	public static String SourceAttachmentForm_message_pressButtonToAttach;
	public static String SourceAttachmentForm_message_noSourceInAttachment;
	public static String SourceAttachmentForm_message_pressButtonToChange;
	public static String SourceAttachmentForm_button_attachSource;
	public static String SourceAttachmentForm_button_changeAttachedSource;
	public static String SourceAttachmentForm_error_title;
	public static String SourceAttachmentForm_error_message;
	public static String SourceAttachmentForm_attach_error_title;
	public static String SourceAttachmentForm_attach_error_message;
	public static String SourceAttachmentForm_message_containerEntry;
	public static String EditorUtility_concatModifierStrings;
	public static String OverrideIndicatorManager_implements;
	public static String OverrideIndicatorManager_intallJob;
	public static String OverrideIndicatorManager_overrides;
	public static String OverrideIndicatorManager_open_error_title;
	public static String OverrideIndicatorManager_open_error_message;
	public static String OverrideIndicatorManager_open_error_messageHasLogEntry;
	public static String SemanticHighlighting_job;
	public static String SemanticHighlighting_field;
	public static String SemanticHighlighting_global_variable;
	public static String SemanticHighlighting_constant;
	public static String SemanticHighlighting_alias;
	public static String SemanticHighlighting_typedef;
	public static String SemanticHighlighting_staticField;
	public static String SemanticHighlighting_methodDeclaration;
	public static String SemanticHighlighting_functionDeclaration;
	public static String SemanticHighlighting_staticMethodInvocation;
	public static String SemanticHighlighting_annotationElementReference;
	public static String SemanticHighlighting_abstractMethodInvocation;
	public static String SemanticHighlighting_inheritedMethodInvocation;
	public static String SemanticHighlighting_localVariableDeclaration;
	public static String SemanticHighlighting_localVariable;
	public static String SemanticHighlighting_parameterVariable;
	public static String SemanticHighlighting_deprecatedMember;
	public static String SemanticHighlighting_typeVariables;
	public static String SemanticHighlighting_method;
	public static String SemanticHighlighting_function;
	public static String SemanticHighlighting_autoboxing;
	public static String SemanticHighlighting_classes;
	public static String SemanticHighlighting_templates;
	public static String SemanticHighlighting_structs;
	public static String SemanticHighlighting_unions;
	public static String SemanticHighlighting_enums;
	public static String SemanticHighlighting_enum_members;
	public static String SemanticHighlighting_interfaces;
	public static String SemanticHighlighting_annotations;
	public static String SemanticHighlighting_typeArguments;
	public static String JavaEditor_markOccurrences_job_name;
	public static String Editor_OpenPropertiesFile_error_keyNotFound;
	public static String Editor_OpenPropertiesFile_error_fileNotFound_dialogMessage;
	public static String Editor_OpenPropertiesFile_error_openEditor_dialogMessage;
	public static String Editor_MoveLines_IllegalMove_status;
	public static String JavaElementHyperlink_hyperlinkText;
	public static String JavaElementHyperlink_hyperlinkText_qualified;
	public static String JavaElementImplementationHyperlink_error_no_implementations_found_message;
	public static String JavaElementImplementationHyperlink_error_status_message;
	public static String JavaElementImplementationHyperlink_hyperlinkText;
	public static String JavaElementImplementationHyperlink_hyperlinkText_qualified;
	public static String JavaElementImplementationHyperlink_search_method_implementors;

	static {
		NLS.initializeMessages(BUNDLE_NAME, JavaEditorMessages.class);
	}

	public static String BasicEditorActionContributor_specific_content_assist_menu;
}