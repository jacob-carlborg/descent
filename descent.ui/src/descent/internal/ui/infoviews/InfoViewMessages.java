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
	public static String JavadocView_error_noBrowser_title;
	public static String JavadocView_error_noBrowser_message;
	public static String JavadocView_error_noBrowser_doNotWarn;
	public static String JavadocView_noAttachedInformation;

	static {
		NLS.initializeMessages(BUNDLE_NAME, InfoViewMessages.class);
	}
}
