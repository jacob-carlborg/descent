package descent.internal.ui.compare;

import org.eclipse.osgi.util.NLS;

public final class CompareMessages extends NLS {

	private static final String BUNDLE_NAME= "descent.internal.ui.compare.CompareMessages";//$NON-NLS-1$

	private CompareMessages() {
		// Do not instantiate
	}

	public static String JarStructureCreator_name;
	public static String JavaMergeViewer_title;
	public static String PropertiesFileMergeViewer_title;
	public static String JavaStructureViewer_title;
	public static String JavaNode_compilationUnit;
	public static String JavaNode_importDeclarations;
	public static String JavaNode_initializer;
	public static String JavaNode_packageDeclaration;
	public static String PropertyCompareViewer_title;
	public static String PropertyCompareViewer_malformedEncoding;
	public static String CompareWithHistory_title;
	public static String CompareWithHistory_internalErrorMessage;
	public static String CompareWithHistory_invalidSelectionMessage;
	public static String ReplaceFromHistory_title;
	public static String ReplaceFromHistory_internalErrorMessage;
	public static String ReplaceFromHistory_invalidSelectionMessage;
	public static String ReplaceFromHistory_parsingErrorMessage;
	public static String AddFromHistory_title;
	public static String AddFromHistory_internalErrorMessage;
	public static String AddFromHistory_invalidSelectionMessage;
	public static String AddFromHistory_noHistoryMessage;
	public static String Editor_Buffer;
	public static String Workspace_File;
	public static String LocalHistoryActionGroup_menu_local_history;
	public static String LocalHistoryActionGroup_action_compare_with;
	public static String LocalHistoryActionGroup_action_compare_with_title;
	public static String LocalHistoryActionGroup_action_compare_with_message;
	public static String LocalHistoryActionGroup_action_replace_with_previous;
	public static String LocalHistoryActionGroup_action_replace_with_previous_title;
	public static String LocalHistoryActionGroup_action_replace_with_previous_message;
	public static String LocalHistoryActionGroup_action_replace_with;
	public static String LocalHistoryActionGroup_action_replace_with_title;
	public static String LocalHistoryActionGroup_action_replace_with_message;
	public static String LocalHistoryActionGroup_action_add;
	public static String LocalHistoryActionGroup_action_add_title;
	public static String LocalHistoryActionGroup_action_add_message;

	static {
		NLS.initializeMessages(BUNDLE_NAME, CompareMessages.class);
	}
}