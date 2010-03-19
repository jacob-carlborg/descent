package scratch.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public abstract class DisplayEvents_TestSnippet {
	
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
		new Button(parent, SWT.PUSH);
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				System.out.println("Runnable");
			}
		};
		display.timerExec(100, runnable);
		
		display.timerExec(150, new Runnable() {
			@Override
			public void run() {
				display.timerExec(1000, runnable);
			}
		});
	}

}