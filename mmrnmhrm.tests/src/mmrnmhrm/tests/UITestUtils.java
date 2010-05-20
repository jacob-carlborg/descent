package mmrnmhrm.tests;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class UITestUtils {
	
	public static void runEventLoop(Shell loopShell) {
		//Use the display provided by the shell if possible
		Display display;
		display = loopShell.getDisplay();

		while (loopShell != null && !loopShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
			
		}
		display.update();
	}
	
}
