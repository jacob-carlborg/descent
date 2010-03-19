 package scratch.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * There is a bug when PaintItem listener is used on the first column, probably Windows-only  
 */
public class Table_PaintBar {
	
	public static void main(String[] args) {
		final Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setBounds(50, 50, 400, 400);
		

			final Table table = new Table(shell, SWT.BORDER);
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			final TableColumn column0 = new TableColumn(table, SWT.NONE);
			column0.setMoveable(true);
			final TableColumn column1 = new TableColumn(table, SWT.NONE);
			final TableColumn column2 = new TableColumn(table, SWT.NONE);
			column0.setWidth(20);
			column1.setWidth(100);
			column2.setWidth(400);
		
			for (int i = 0; i < 6; i++) {
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText("Row " + i + " (BLAH) ");
			}
			
			/*
			 * NOTE: MeasureItem, PaintItem and EraseItem are called repeatedly.
			 * Therefore, it is critical for performance that these methods be
			 * as efficient as possible.
			 */
			table.addListener(SWT.PaintItem, new Listener() {
				public void handleEvent(Event event) {
					GC gc = event.gc;
					Color foreground = gc.getForeground();
					Color background = gc.getBackground();
					TableItem item = (TableItem) event.item;

					System.out.println(event.index + " " + event);
					if (event.index == table.indexOf(column1)) {
						int percent = 100;
						gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
						gc.setForeground(display.getSystemColor(SWT.COLOR_YELLOW));
						int width = (column1.getWidth() - 1) * percent / 100;
						gc.fillGradientRectangle(event.x, event.y, width, event.height, false);
						Rectangle rect2 = new Rectangle(event.x, event.y, 100, event.height - 1);
						gc.fillRectangle(rect2);
//				gc.setForeground(display.getSystemColor(SWT.COLOR_LIST_FOREGROUND));
//				String text = percent+"%";
//				Point size = event.gc.textExtent(text);					
//				int offset = Math.max(0, (event.height - size.y) / 2);
//				gc.drawText(text, event.x+2, event.y+offset, true);
					} else {
						int rowIx = table.indexOf((TableItem) event.item);
						System.out.println("Paint @: " + rowIx +","+ event.index);
						gc.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
						gc.fillRectangle(event.x, event.y, 50, event.height);
					}
					gc.setForeground(background);
					gc.setBackground(foreground);
					
				}
			});

		//shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}