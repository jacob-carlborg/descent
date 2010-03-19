package scratch.swt;

import static melnorme.miscutil.Assert.assertNotNull;
import static melnorme.miscutil.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class Table_EraseItem {
	
	private static final int ROWCOUNT = 8;
	protected static Display display;
	protected static Shell shell;
	private Table table;
	
	public static void main(String[] args) {
		display = new Display();
		shell = new Shell(display);
		shell.setBounds(50, 50, 400, 400);
		shell.setLayout(new FillLayout());
		shell.setText(Table_EraseItem.class.getSimpleName());
		new Table_EraseItem().run();
	}
	
	public Table_EraseItem() {
	}
	
	protected void runEventLoop() {
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	
	
	public void run() {
		
		table = new Table(shell, SWT.FULL_SELECTION | SWT.MULTI | SWT.NO_BACKGROUND);
		
		table.setHeaderVisible(true);
		final TableColumn column2 = new TableColumn(table, SWT.NONE);
		TableColumn column1 = new TableColumn(table, SWT.NONE);
		column1.setWidth(100);
		column1.setMoveable(true);
		column2.setWidth(100);
		column2.setMoveable(true);
		
		table.setBackgroundImage(new Image(display, "foo.jpg"));
		
		final int clientWidth = table.getClientArea().width;
		table.addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				event.width = clientWidth * 2;
			}
		});
		
		table.addListener(SWT.EraseItem, new Listener() {
			public void handleEvent(Event event) {
				doErase(event);
			}
		});
		
		table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				//table.deselectAll();
			}
		});
		
		for (int i = 0; i < ROWCOUNT; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(1, "Row " + i + " (BLAH) ");
			item.setText(0, "Foo " + i);
			if(i == 2 || i == 4 || i == 6) {
				item.setBackground(display.getSystemColor(SWT.COLOR_MAGENTA));
				assertTrue(item.getBackground(1).equals(display.getSystemColor(SWT.COLOR_MAGENTA)));
				
				item.setBackground(1, display.getSystemColor(SWT.COLOR_RED));
				assertTrue(item.getBackground(1).equals(display.getSystemColor(SWT.COLOR_RED)));
				
				item.setBackground(1, null);
				assertTrue(item.getBackground(1).equals(display.getSystemColor(SWT.COLOR_MAGENTA)));
			}
		}
		table.setSelection(1, 3);
		
		runEventLoop();
	}
	
	private void doErase(Event event) {
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
		
		int rowIx = table.indexOf((TableItem) event.item);
		System.out.println("EraseItem: " + rowIx +","+ event.index + " " + erases);


		TableItem item = (TableItem) event.item;
		GC gc = event.gc;
		

		// Unfortunately the presence of either HOT or SELECTED will cause 
		// the background not to be drawn on the entire row
		if((event.detail & (SWT.SELECTED | SWT.HOT)) != 0) {

			Color background = item.getBackground(event.index);
			assertNotNull(background);
			if((event.detail & (SWT.BACKGROUND | SWT.SELECTED)) != 0) {
				gc.setBackground(background);
				gc.fillRectangle(event.x, event.y, event.width, event.height);
			}

			gc.setBackground(display.getSystemColor(SWT.COLOR_GREEN)); // Apparently has no effect
//			gc.setForeground(display.getSystemColor(SWT.COLOR_RED)); // Apparently changes the color of text in that cell
//			gc.setForeground(item.getForeground(event.index)); // Restore normal foreground color for FOREGROUND draw 
			
			event.detail &= ~(SWT.BACKGROUND); // Apparently this has no effect. (Cannot find one at least) 
//			event.detail |= (SWT.BACKGROUND);
		}
			

		if((event.detail & (SWT.SELECTED)) != 0) {
			event.detail &= ~SWT.SELECTED; // Unfortunately, removing SELECTED from event.detail will cause HOT not to be processed
		}
		
//		event.detail &= ~SWT.BACKGROUND;
	}

}
