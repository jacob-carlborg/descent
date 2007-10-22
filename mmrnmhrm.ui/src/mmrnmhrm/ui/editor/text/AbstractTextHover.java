package mmrnmhrm.ui.editor.text;

import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.DeePluginPreferences;

import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.JDT_PreferenceConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.ITextEditor;

import descent.internal.ddoc.IDeeDocColorConstants;

@SuppressWarnings("restriction")
public class AbstractTextHover extends org.eclipse.dltk.internal.ui.text.hover.DocumentationHover 
implements ITextHoverExtension {

	private static final String CODE_CSS_CLASS 
	= ".code		 { font-family: monospace; background-color: #e7e7e8; border: 2px solid #cccccc; padding: 0ex;}";

	/** The CSS used to format javadoc information. */
	protected static String fgCSSStyles;

	protected ITextEditor fEditor;

	
	public AbstractTextHover(ISourceViewer sourceViewer) {
		super();
		setPreferenceStore(DeePlugin.getPrefStore());
		//super(sourceViewer);
	}

	@SuppressWarnings("restriction")
	protected String getCSSStyles() {
		if(false)
			return getStyleSheet() + CODE_CSS_CLASS;
		
		if (fgCSSStyles == null) {
			fgCSSStyles= HoverUtil.loadStyleSheet("/JavadocHoverStyleSheet.css");
		}
		String css= fgCSSStyles;
		if (css != null) {
			FontData fontData= JFaceResources.getFontRegistry().getFontData(JDT_PreferenceConstants.APPEARANCE_JAVADOC_FONT)[0];
			css= org.eclipse.jface.internal.text.html.
				HTMLPrinter.convertTopLevelFont(css, fontData);
		}
		StringBuffer strBuf = new StringBuffer(css);
		strBuf.append(CODE_CSS_CLASS);
		addPreferencesFontsAndColorsToStyleSheet(strBuf);
		return strBuf.toString();
	}

	
	private static void addPreferencesFontsAndColorsToStyleSheet(StringBuffer buffer) {
		addStyle(buffer, IDeeDocColorConstants.JAVA_KEYWORD);
		addStyle(buffer, IDeeDocColorConstants.JAVA_KEYWORD_RETURN);
		addStyle(buffer, IDeeDocColorConstants.JAVA_SPECIAL_TOKEN);
		addStyle(buffer, IDeeDocColorConstants.JAVA_OPERATOR);
		addStyle(buffer, IDeeDocColorConstants.JAVA_DEFAULT);
		addStyle(buffer, IDeeDocColorConstants.JAVA_PRAGMA);
		addStyle(buffer, IDeeDocColorConstants.JAVA_STRING);
		addStyle(buffer, IDeeDocColorConstants.JAVA_SINGLE_LINE_COMMENT);
		addStyle(buffer, IDeeDocColorConstants.JAVA_SINGLE_LINE_DOC_COMMENT);
		addStyle(buffer, IDeeDocColorConstants.JAVA_MULTI_LINE_COMMENT);
		addStyle(buffer, IDeeDocColorConstants.JAVA_MULTI_LINE_PLUS_COMMENT);
		addStyle(buffer, IDeeDocColorConstants.JAVA_MULTI_LINE_PLUS_DOC_COMMENT);
		addStyle(buffer, IDeeDocColorConstants.JAVADOC_DEFAULT);
	}
	
	private static void addStyle(StringBuffer buffer, String partialPreferenceKey) {
		//IJavaProject javaProject = null;
		
		buffer.append("."); //$NON-NLS-1$
		buffer.append(partialPreferenceKey);
		buffer.append("{"); //$NON-NLS-1$

		String colorString = DeePluginPreferences.getPreference(partialPreferenceKey, null);
		RGB color = colorString == null ? new RGB(0, 0, 0) : StringConverter.asRGB(colorString);
		buffer.append("color: "); //$NON-NLS-1$
		HTMLPrinter_appendColor(buffer, color);
		buffer.append(";"); //$NON-NLS-1$
		
		String boolString;
		boolean bool, bool2;
		
		boolString = DeePluginPreferences.getPreference(partialPreferenceKey + PreferenceConstants.EDITOR_BOLD_SUFFIX, null);
		bool = convertToBool(boolString);
		if (bool) {
			buffer.append("font-weight: bold;"); //$NON-NLS-1$
		}
		
		boolString = DeePluginPreferences.getPreference(partialPreferenceKey + PreferenceConstants.EDITOR_ITALIC_SUFFIX, null);
		bool = convertToBool(boolString);
		if (bool) {
			buffer.append("font-style: italic;"); //$NON-NLS-1$
		}
		
		boolString = DeePluginPreferences.getPreference(partialPreferenceKey + PreferenceConstants.EDITOR_UNDERLINE_SUFFIX, null);
		bool = convertToBool(boolString);
		
		boolString = DeePluginPreferences.getPreference(partialPreferenceKey + PreferenceConstants.EDITOR_STRIKETHROUGH_SUFFIX, null);
		bool2 = convertToBool(boolString);
		
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

	private static boolean convertToBool(String boolString) {
		return boolString == null || boolString.equals("") ? 
				false : StringConverter.asBoolean(boolString);
	}

	
	public static void HTMLPrinter_appendColor(StringBuffer buffer, RGB rgb) {
		buffer.append('#');
		buffer.append(toHexString(rgb.red));
		buffer.append(toHexString(rgb.green));
		buffer.append(toHexString(rgb.blue));
	}
	
	private static String toHexString(int value) {
		String s = Integer.toHexString(value);
		if (s.length() != 2) {
			return "0" + s;
		}
		return s;
	}

	@Override
	@SuppressWarnings("restriction")
	public IInformationControlCreator getHoverControlCreator() {
		if(true)
		return super.getHoverControlCreator();
		
		if(false)
		return new IInformationControlCreator() {
			@SuppressWarnings("restriction")
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, SWT.NONE, 
						//new	org.eclipse.jface.internal.text.html.HTMLTextPresenter(true),
						new org.eclipse.dltk.internal.ui.text.HTMLTextPresenter(true),
						EditorsUI.getTooltipAffordanceString());
			}
		};
		
		Shell shell= JavaPlugin.getActiveWorkbenchShell();
		if (shell == null
				|| !org.eclipse.dltk.internal.ui.BrowserInformationControl
						.isAvailable(shell))
			return null;
		
		return new AbstractReusableInformationControlCreator() {
			@SuppressWarnings("restriction")
			@Override
			public IInformationControl doCreateInformationControl(Shell parent) {
				return new org.eclipse.dltk.internal.ui.BrowserInformationControl(
						parent, SWT.NO_TRIM, SWT.NONE, "STATUS");
			}
		};
	}


}