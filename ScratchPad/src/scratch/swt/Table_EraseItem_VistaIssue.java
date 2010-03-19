package scratch.swt;

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

public class Table_EraseItem_VistaIssue {
	
	protected static Display display;
	protected static Shell shell;
	protected static Table table;
	
	public static void main(String[] args) {
		display = new Display();
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
		run();
		
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	
	
	public static void run() {
		table = new Table(shell, SWT.FULL_SELECTION | SWT.MULTI);
		new TableColumn(table, SWT.NONE).setWidth(100);
		new TableColumn(table, SWT.NONE).setWidth(100);
		new TableColumn(table, SWT.NONE).setWidth(100);
		
		table.addListener(SWT.EraseItem, new Listener() {
			public void handleEvent(Event event) {
				doErase(event);
			}
		});

		for (int i = 0; i < 6; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, "Foo " + i);
			item.setText(1, "Row " + i + " (BLAH) ");
			item.setText(2, "Foo " + i);
			if(i == 2 || i == 4) {
				item.setBackground(display.getSystemColor(SWT.COLOR_MAGENTA));
			}
		}
	}
	
	private static void doErase(Event event) {
		int rowIx = table.indexOf((TableItem) event.item);

		TableItem item = (TableItem) event.item;
		GC gc = event.gc;

		if(rowIx == 2) {
			Color background = item.getBackground(event.index);
			gc.setBackground(background);
			gc.fillRectangle(event.x, event.y, event.width, event.height);
			event.detail &= ~SWT.BACKGROUND;
		}
	}
}
