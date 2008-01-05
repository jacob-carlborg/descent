package descent.internal.ui.infoviews;

import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;

import descent.core.IJavaProject;
import descent.internal.ui.text.HTMLPrinter;
import descent.ui.PreferenceConstants;
import descent.ui.text.IJavaColorConstants;

/**
 * Helper class to build views and popups that shows ddoc.
 */
public class JavadocViewHelper {
	
	public static void addPreferencesFontsAndColorsToStyleSheet(StringBuffer buffer) {
		addStyle(buffer, IJavaColorConstants.JAVA_KEYWORD);
		addStyle(buffer, IJavaColorConstants.JAVA_KEYWORD_RETURN);
		addStyle(buffer, IJavaColorConstants.JAVA_SPECIAL_TOKEN);
		addStyle(buffer, IJavaColorConstants.JAVA_OPERATOR);
		addStyle(buffer, IJavaColorConstants.JAVA_DEFAULT);
		addStyle(buffer, IJavaColorConstants.JAVA_PRAGMA);
		addStyle(buffer, IJavaColorConstants.JAVA_STRING);
		addStyle(buffer, IJavaColorConstants.JAVA_SINGLE_LINE_COMMENT);
		addStyle(buffer, IJavaColorConstants.JAVA_SINGLE_LINE_DOC_COMMENT);
		addStyle(buffer, IJavaColorConstants.JAVA_MULTI_LINE_COMMENT);
		addStyle(buffer, IJavaColorConstants.JAVA_MULTI_LINE_PLUS_COMMENT);
		addStyle(buffer, IJavaColorConstants.JAVA_MULTI_LINE_PLUS_DOC_COMMENT);
		addStyle(buffer, IJavaColorConstants.JAVADOC_DEFAULT);
	}
	
	public static void addStyle(StringBuffer buffer, String partialPreferenceKey) {
		IJavaProject javaProject = null;
		
		buffer.append("."); //$NON-NLS-1$
		buffer.append(partialPreferenceKey);
		buffer.append("{"); //$NON-NLS-1$

		String colorString = PreferenceConstants.getPreference(partialPreferenceKey, javaProject);
		RGB color = colorString == null ? new RGB(0, 0, 0) : StringConverter.asRGB(colorString);
		buffer.append("color: "); //$NON-NLS-1$
		HTMLPrinter.appendColor(buffer, color);
		buffer.append(";"); //$NON-NLS-1$
		
		String boolString;
		boolean bool, bool2;
		
		boolString = PreferenceConstants.getPreference(partialPreferenceKey + PreferenceConstants.EDITOR_BOLD_SUFFIX, javaProject);
		bool = boolString == null ? false : StringConverter.asBoolean(boolString);
		if (bool) {
			buffer.append("font-weight: bold;"); //$NON-NLS-1$
		}
		
		boolString = PreferenceConstants.getPreference(partialPreferenceKey + PreferenceConstants.EDITOR_ITALIC_SUFFIX, javaProject);
		bool = boolString == null ? false : StringConverter.asBoolean(boolString);
		if (bool) {
			buffer.append("font-style: italic;"); //$NON-NLS-1$
		}
		
		boolString = PreferenceConstants.getPreference(partialPreferenceKey + PreferenceConstants.EDITOR_UNDERLINE_SUFFIX, javaProject);
		bool = boolString == null ? false : StringConverter.asBoolean(boolString);
		
		boolString = PreferenceConstants.getPreference(partialPreferenceKey + PreferenceConstants.EDITOR_STRIKETHROUGH_SUFFIX, javaProject);
		bool2 = boolString == null ? false : StringConverter.asBoolean(boolString);
		
		if (bool || bool2) {
			buffer.append("text-decoration:"); //$NON-NLS-1$
			if (bool) {
				buffer.append("underline"); //$NON-NLS-1$
			}
			if (bool && bool2) {
				buffer.append(", "); //$NON-NLS-1$
			}
			if (bool2) {
				buffer.append("line-through"); //$NON-NLS-1$
			}
			buffer.append(";"); //$NON-NLS-1$
		}
		
		buffer.append("}\n"); //$NON-NLS-1$
	}

}
