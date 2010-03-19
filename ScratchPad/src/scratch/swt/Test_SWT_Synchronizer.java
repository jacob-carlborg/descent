package scratch.swt;

import melnorme.miscutil.MiscUtil;

import org.eclipse.swt.widgets.Display;
import org.junit.Test;
/**
 * Lets check in which order syncs and asyncs are executed, 
 * both in raw SWT applications and with the Workbench Synchronizer  
 *
 */
public class Test_SWT_Synchronizer {

	public static void main(String[] args) {
		new Test_SWT_Synchronizer().doTest();
	}


	@Test
	public void doTest() {
		final SWTApp app = new SWTApp() { };
		eventSequence("UI: ", true);
		
		final Thread thread = new Thread() {
			@Override
			public void run() {
				eventSequence("Thread: ", true);
			}
		};
		thread.start();
		
		app.createAndRunApplication();
	}

	
	private static void eventSequence(final String str, boolean runNested) {
		Display.getDefault().asyncExec(createPrinterRunnable(str + "1"));
		Display.getDefault().asyncExec(createPrinterRunnable(str + "2"));
		Display.getDefault().asyncExec(createPrinterRunnable(str + "3"));
		Display.getDefault().syncExec(createPrinterRunnable(str + "1 sync"));
		if(runNested) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					eventSequence("UI NESTED::", false);
				}
			});
		}
		Display.getDefault().syncExec(createPrinterRunnable(str + "2 sync"));
		Display.getDefault().syncExec(createPrinterRunnable(str + "3 sync"));
		
	}


	private static Runnable createPrinterRunnable(final String str) {
		return new Runnable() {
			@Override
			public void run() {
				System.out.println(str);
				MiscUtil.sleepUnchecked(100);
			}
		};
	}
}
