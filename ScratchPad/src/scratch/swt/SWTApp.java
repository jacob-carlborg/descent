package scratch.swt;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class SWTApp {
	
	public Display display;
	public Shell shell;
	
	public SWTApp() {
	}
	
	public void createShell() {
		display = Display.getDefault();
		shell = new Shell(display);
		shell.setText(getClass().getSimpleName());
		shell.setBounds(50, 50, 450, 450);
		shell.setLayout(new FillLayout());
	}
	
	public void disposeShell() {
		if(!shell.isDisposed())
			shell.dispose();
	}
	
	public void createAndRunApplication() {
		createShell();
		createShellContents();
		shell.open();
		runEventLoop();
		display.dispose();
	}
	
	protected void createShellContents() { }

	public void runEventLoop() {
		runEventLoop(display, shell);
	}
	
	public void runEventLoop(final AtomicBoolean finished) {
		while (!shell.isDisposed() && !finished.get()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	/* Warning, this method only terminates when shell is disposed */
	public static void runEventLoop(Display display, Shell shell) {
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
}