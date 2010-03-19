package scratch.swt;

import static melnorme.miscutil.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class Table_EraseItemXXX {
	
	protected static Display display;
	protected static Shell shell;
	
	public static void main(String[] args) {
		display = new Display();
		shell = new Shell(display);
		shell.setBounds(50, 50, 400, 400);
		shell.setLayout(new FillLayout());
		new Table_EraseItemXXX().run(args);
	}
	
	public Table_EraseItemXXX() {
	}
	
	protected void runEventLoop() {
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	
	
	public void run(String[] args) {
		final Color red = display.getSystemColor(SWT.COLOR_RED);
		final Color yellow = display.getSystemColor(SWT.COLOR_YELLOW);
		
		//final Table table = new Table(shell, SWT.FULL_SELECTION | SWT.MULTI);
		final Table table = new Table(shell, SWT.BORDER);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		TableColumn column1 = new TableColumn(table, SWT.NONE);
		TableColumn column2 = new TableColumn(table, SWT.NONE);
		column1.setWidth(500);
		column2.setWidth(100);

		//final Image parliamentImage = new Image(display, "./foo.jpg");
		//table.setBackgroundImage(parliamentImage);

//		final int clientWidth = table.getClientArea().width;
//		table.addListener(SWT.MeasureItem, new Listener() {
//			public void handleEvent(Event event) {
//				event.width = clientWidth * 2;
//			}
//		});
		
//		table.addListener(SWT.EraseItem, new Listener() {
//			public void handleEvent(Event event) {
//				doEraseItem(event);
//			}
//		});
		
		for (int i = 0; i < 6; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText("Row " + i + " (BLAH) ");
		}
		
		table.addListener(SWT.PaintItem, new Listener() {
			public void handleEvent(Event event) {
				GC gc = event.gc;
				Color oldForeground = gc.getForeground();
				Color oldBackground = gc.getBackground();
				gc.setForeground(yellow);
				gc.setBackground(red);
				gc.fillGradientRectangle(0, event.y + 3, 200, 2, false);
				gc.setBackground(oldBackground);
				gc.setForeground(oldForeground);
			}
		});
		
		runEventLoop();
	}
	
	private void doEraseItem(Event event) {
		final Color blue = display.getSystemColor(SWT.COLOR_BLUE);
		final Color white = display.getSystemColor(SWT.COLOR_WHITE);
		
		String erases = "";
		if ((event.detail & SWT.SELECTED) != 0) {
			erases += "SELECTED ";
		}
		if ((event.detail & SWT.FOCUSED) != 0) {
			erases += "FOCUSED ";
		}
		if ((event.detail & SWT.BACKGROUND) != 0) {
			erases += "BACKGROUND ";
		}
		if ((event.detail & SWT.FOREGROUND) != 0) {
			erases += "FOREGROUND ";
		}
		if ((event.detail & SWT.HOT) != 0) {
			erases += "HOT ";
		}
		
		System.out.println(erases);
		
		assertTrue((event.detail & SWT.BACKGROUND) == 0);
		GC gc = event.gc;
//		Rectangle clipping = gc.getClipping();
		
		Color oldForeground = gc.getForeground();
		Color oldBackground = gc.getBackground();
		gc.setForeground(blue);
		gc.setBackground(white);
		gc.fillGradientRectangle(0, event.y, 200, event.height, false);
		gc.setForeground(oldForeground);
		gc.setBackground(oldBackground);
		//event.detail &= ~SWT.BACKGROUND;
		event.detail &= ~SWT.HOT;
	}
	

}
