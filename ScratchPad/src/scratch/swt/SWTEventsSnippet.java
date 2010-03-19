package scratch.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public abstract class SWTEventsSnippet {
	
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
		new Button(parent, SWT.NONE);
		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				runBasic();
			}
		});
	}

	protected static void runBasic() {
		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				System.out.println("A");
				Display.getCurrent().asyncExec(new Runnable() {
					@Override
					public void run() {
						System.out.println("B");
					}
				});
			}
		});
		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				System.out.println("A2");
				Display.getCurrent().asyncExec(new Runnable() {
					@Override
					public void run() {
						System.out.println("B2");
					}
				});
			}
		});
	}

}