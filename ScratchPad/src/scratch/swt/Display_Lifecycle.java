package scratch.swt;

import static melnorme.miscutil.Assert.assertTrue;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public abstract class Display_Lifecycle {
	
	protected static Display display;
	protected static Shell shell;

	public static void main(String[] args) {
		display = new Display();
		shell = new Shell(display);
		shell.setBounds(50, 50, 400, 400);
		shell.setLayout(new FillLayout());
		
		assertTrue(Display.getDefault() == display);
		display.dispose();
		assertTrue(Display.getCurrent() == display);
		assertTrue(Display.getDefault() != display);
		assertTrue(Display.getCurrent() != display);
	}

}