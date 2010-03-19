package scratch.swt;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;

import scratch.utils.NewUtils;

public abstract class ScrollbarsSnippet {
	
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
	
	public static int xDocumentWidth = 900;
	
	int xoffset;
	int yoffset;
	private static Canvas canvas;
	
	private static void create(Composite parent) {
		canvas = new Canvas(parent, SWT.DOUBLE_BUFFERED | SWT.NO_REDRAW_RESIZE | SWT.V_SCROLL | SWT.H_SCROLL);
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				doPaint(e);
			}
		});
		canvas.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				updateScrollBar();
				canvas.redraw();
			}
		});
		
		final ScrollBar verticalBar = canvas.getVerticalBar();
		final ScrollBar horizontalBar = canvas.getHorizontalBar();
		verticalBar.setValues(0, 0, 100, 1, 1, 10);
		horizontalBar.setValues(0, 0, xDocumentWidth, 10, 10, 30);
		final SelectionAdapter listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				canvas.redraw();
			}
		};
		verticalBar.addSelectionListener(listener);
		horizontalBar.addSelectionListener(listener);
	}

	
	protected static void updateScrollBar() {
		final int xSize = canvas.getClientArea().width;
		int handleSize = xSize;
		
		final ScrollBar horizontalBar = canvas.getHorizontalBar();
		horizontalBar.setValues(0, 0, xDocumentWidth, handleSize, 100, handleSize);
	}

	protected static void doPaint(PaintEvent e) {
		final GC gc = e.gc;
		gc.drawText("Foo: " + NewUtils.TIMESTAMP_FORMAT.format(new Date()), 10, 10);
		gc.drawText("CA: " + canvas.getClientArea(), 80, 10);
		final ScrollBar hBar = canvas.getHorizontalBar();
		final ScrollBar vBar = canvas.getVerticalBar();
		gc.drawText("X: " + hBar.getSelection(), 10, 30);
		gc.drawText("Y: " + vBar.getSelection(), 10, 50);
		
	}

}