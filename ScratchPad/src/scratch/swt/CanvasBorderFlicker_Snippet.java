package scratch.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;


/**
 * Border flicker when canvas is scrolled. XXX: Vista only?
 */
public abstract class CanvasBorderFlicker_Snippet {
	
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
		final Canvas canvas = new Canvas(parent, 
				SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		
		canvas.getVerticalBar().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final ScrollBar verticalBar = canvas.getVerticalBar();
				System.out.println("Vbar selection: " + verticalBar.getSelection() + 
						" " + canvas.getClientArea());
//				canvas.setRedraw(false);
				for (int i = 0; i < 100; i++) {
					canvas.redraw(); canvas.update();
				}
//				canvas.setRedraw(true);

			}
		});
		canvas.getVerticalBar().setMaximum(20);
		canvas.getVerticalBar().setSelection(5);
	}


}