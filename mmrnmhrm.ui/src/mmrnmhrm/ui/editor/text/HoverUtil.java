package mmrnmhrm.ui.editor.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.osgi.framework.Bundle;

import dtool.dom.definitions.DefUnit;

public class HoverUtil {

	public static String getDefUnitHoverInfoWithDeeDoc(DefUnit defUnit) {
		String sig = defUnit.toStringFullSignature();
		String str = sig.substring(sig.indexOf(' ')+1);
		str = "<b>" +str+ "</b>" 
		+"  <span style=\"color: #915F6D;\" >" +
			"("+defUnit.getArcheType().toString()+")" +"</span>"; 
		str = str + "<br/> <p>DeeDoc goes here.</p>" ;
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

}
