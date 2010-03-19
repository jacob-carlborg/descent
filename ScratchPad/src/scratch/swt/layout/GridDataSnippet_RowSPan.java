package scratch.swt.layout;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public abstract class GridDataSnippet_RowSPan {
	
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
		parent.setLayout(new GridLayout(1, false));
		parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA));
		
		Composite c1 = new Composite(parent, SWT.NONE);
		c1.setLayoutData(GridDataFactory.swtDefaults().grab(false, false).hint(250, 100).create());

		Composite c2 = new Composite(parent, SWT.NONE);
		c2.setLayoutData(GridDataFactory.swtDefaults().grab(false, false).hint(120, 120).create());
		
		parent.layout(true, true);
	}

}