package mmrnmhrm.tests;

import melnorme.miscutil.ExceptionAdapter;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class UITestUtils {

	public static void flushUI() {
		// how to redray display?
		if(true) return;
	
		while(Display.getCurrent().readAndDispatch() == true)
			;

		Display.getCurrent().update();
		
		Display.getCurrent().syncExec(new Runnable() {
			public void run() {
				try {
					//if(false)
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					throw ExceptionAdapter.unchecked(e);
				}
			}});

	}
	
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
