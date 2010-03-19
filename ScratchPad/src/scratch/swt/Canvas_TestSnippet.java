package scratch.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public abstract class Canvas_TestSnippet {
	
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
	
	private static PaintListener paintListener = new PaintListener() {
		@Override
		public void paintControl(PaintEvent e) {
			Canvas canvas = (Canvas) e.widget;
			System.out.println(canvas.getBorderWidth());
			System.out.println(canvas.getClientArea());
			GC gc = e.gc;
			gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			gc.drawRectangle(0, 0, 10, 10);
			gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
			gc.fillRectangle(0, 0, 10, 20);
		}
	};
	
	private static void create(Composite parent) {
		Canvas canvas = new Canvas(parent, SWT.BORDER);
		canvas.addPaintListener(paintListener);
		Canvas canvas2 = new Canvas(parent, SWT.NONE);
		canvas2.addPaintListener(paintListener);
	}
	
}