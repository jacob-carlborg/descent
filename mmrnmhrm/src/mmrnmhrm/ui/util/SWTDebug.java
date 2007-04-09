package mmrnmhrm.ui.util;

import java.util.Random;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class SWTDebug {
		
	//public static boolean debug = true;
	public static boolean debug = false;
	
	public static Random rnd = new Random(0);

	public static void setColor(Composite composite, int id) {
		if(debug)
			composite.setBackground(Display.getDefault().getSystemColor(id));
	}
	
	public static void setColor(Composite composite, Color color) {
		if(debug)
			composite.setBackground(color);
	}
	
	public static void setRandomColor(Composite composite) {
		if(debug) {
			Color color = new Color(Display.getDefault(), 
					rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
			composite.setBackground(color);
		}
	}

}
