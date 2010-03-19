package scratch.utils;


import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SWTUtils {
	
	/** Runs the event queue until there are no events in the queue. */
	public static void runPendingUIEvents(Display display, Shell shell) {
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) 
				break;
		}
	}
	
	/** Runs the event queue until there are no events in the queue. */
	public static void runPendingUIEvents(Display display) {
		while (display.readAndDispatch()) {
		}
	}
	
	public static Color getSystemColor(int colorId) {
		return Display.getCurrent().getSystemColor(colorId);
	}
	
}
