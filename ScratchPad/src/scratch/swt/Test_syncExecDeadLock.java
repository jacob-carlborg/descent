package scratch.swt;

import melnorme.miscutil.MiscUtil;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Test_syncExecDeadLock {

	protected static Display display;
	protected static Shell shell;

	public static void main(String[] args) {
		display = new Display();
		shell = new Shell(display);
		shell.setBounds(50, 50, 400, 400);
		shell.setLayout(new FillLayout());
		create(shell);
		runEventLoop();
	}
	
	protected static void runEventLoop() {
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
	}
	
	private static void create(Composite parent) {
		final SWTApp app = new SWTApp() { };

		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				foo();
			}
		});

		app.createAndRunApplication();
	}

	private static void foo() {
		final Thread thread = new Thread() {
			@Override
			public void run() {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						Shell shell = Display.getCurrent().getActiveShell();
						MessageDialog.openInformation(shell, "title", "message");
					}
				});
			}
		};

		thread.start();
		
		MiscUtil.sleepUnchecked(200);

		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				Shell shell = Display.getCurrent().getActiveShell();
				MessageDialog.openInformation(shell, "title2", "message2");
			}
		});
		
		
	}

}
