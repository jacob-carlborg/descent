package mmrnmhrm.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class SWTDebug {
	
	//public static boolean debug = true;
	public static boolean debug = false;

	public static void setColor(Composite stylesComposite, int id) {
		if(debug)
			stylesComposite.setBackground(Display.getDefault().getSystemColor(id));
		
	}

}
