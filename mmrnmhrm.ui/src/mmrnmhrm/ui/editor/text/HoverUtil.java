package mmrnmhrm.ui.editor.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.FontData;
import org.osgi.framework.Bundle;

import dtool.ast.definitions.DefUnit;

public class HoverUtil {

	public static String getDefUnitHoverInfoWithDeeDoc(DefUnit defUnit) {
		String sig = defUnit.toStringForHoverSignature();
		String str = sig.substring(sig.indexOf(' ')+1);
		str = convertToHTMLContent(str);
		str = "<b>" +str+ "</b>" 
		+"  <span style=\"color: #915F6D;\" >" +
			"("+defUnit.getArcheType().toString()+")" +"</span>";
		
		String docComments = defUnit.getCombinedDocComments(); 
		if(docComments == null) {
			//str = str + "<br/> <p>No DeeDoc present.</p>";
		} else 
			str = str + "<br/><br/>" + convertToHTMLContent(docComments);
		return str;
	}

	@SuppressWarnings("restriction")
	private static String convertToHTMLContent(String str) {
		str = org.eclipse.jface.internal.text.html.
			HTMLPrinter.convertToHTMLContent(str);
		str = str.replace("\n", "<br/>");
		return str;
	}

	public static String loadStyleSheet(String cssfilepath) {
		Bundle bundle= Platform.getBundle(JavaPlugin.getPluginId());
		URL url= bundle.getEntry(cssfilepath); //$NON-NLS-1$
		if (url != null) {
			try {
				url= FileLocator.toFileURL(url);
				BufferedReader reader= new BufferedReader(new InputStreamReader(url.openStream()));
				StringBuffer buffer= new StringBuffer(200);
				String line= reader.readLine();
				while (line != null) {
					buffer.append(line);
					buffer.append('\n');
					line= reader.readLine();
				}
				return buffer.toString();
			} catch (IOException ex) {
				JavaPlugin.log(ex);
			}
		}
		return null;
	}

	@SuppressWarnings("restriction")
	public static String getCompleteHoverInfo(String info, String cssStyle) {
		
		if (info != null && info.length() > 0) {
			StringBuffer buffer= new StringBuffer();
			org.eclipse.jface.internal.text.html.
			HTMLPrinter.insertPageProlog(buffer, 0, cssStyle);
			buffer.append(info);
			org.eclipse.jface.internal.text.html.
			HTMLPrinter.addPageEpilog(buffer);
			info= buffer.toString();
		}
		return info;
	}

	@SuppressWarnings("restriction")
	static String setupCSS(String fgCSSStyles) {
		String css= fgCSSStyles;
		if (css != null) {
			FontData fontData= JFaceResources.getFontRegistry().getFontData(PreferenceConstants.APPEARANCE_JAVADOC_FONT)[0];
			css= org.eclipse.jface.internal.text.html.
				HTMLPrinter.convertTopLevelFont(css, fontData);
		}
		return css;
	}

}
