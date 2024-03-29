package descent.internal.ui.infoviews;

import org.eclipse.osgi.util.NLS;

/**
 * Helper class to get NLSed messages.
 */
final class InfoViewMessages extends NLS {

	private static final String BUNDLE_NAME= InfoViewMessages.class.getName();

	private InfoViewMessages() {
		// Do not instantiate
	}

	public static String CopyAction_label;
	public static String CopyAction_tooltip;
	public static String CopyAction_description;

	public static String SelectAllAction_label;
	public static String SelectAllAction_tooltip;
	public static String SelectAllAction_description;

	public static String GotoInputAction_label;
	public static String GotoInputAction_tooltip;
	public static String GotoInputAction_description;

	public static String CopyToClipboard_error_title;
	public static String CopyToClipboard_error_message;

	public static String JavadocView_action_back_disabledTooltip;
	public static String JavadocView_action_back_enabledTooltip;
	public static String JavadocView_action_back_name;
	public static String JavadocView_action_forward_disabledTooltip;
	public static String JavadocView_action_forward_enabledTooltip;
	public static String JavadocView_action_forward_name;
	public static String JavadocView_action_toggleLinking_toolTipText;
	public static String JavadocView_action_toogleLinking_text;
	public static String JavadocView_error_noBrowser_title;
	public static String JavadocView_error_noBrowser_message;
	public static String JavadocView_error_noBrowser_doNotWarn;
	public static String JavadocView_noAttachments;
	public static String JavadocView_noAttachedSource;
	public static String JavadocView_noAttachedJavadoc;
	public static String JavadocView_noInformation;
	public static String JavadocView_error_gettingJavadoc;
	public static String JavadocView_constantValue_hexValue;

	static {
		NLS.initializeMessages(BUNDLE_NAME, InfoViewMessages.class);
	}
}
