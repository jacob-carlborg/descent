package scratch.swt.performance;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


public class Table_Performance_Snippet {
	
	protected Display display;
	protected Shell shell;

	public Table_Performance_Snippet() {
		display = new Display();
		shell = new Shell(display);
		shell.setBounds(50, 50, 400, 400);
		shell.setLayout(new FillLayout());
	}
	
	protected void runEventLoop() {
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
	}
	
	
	public static final DateFormat TIMESTAMPSHORT_FORMAT = new SimpleDateFormat("ss.SSS");
	
	protected static final int ROW_COUNT = 100;
	protected static final int NUM_COLUMNS = 50;
	
	private Table table;
	
	public static void main(String[] args) {
		new Table_Performance_Snippet().run();
	}

	protected void run() {
		shell.setLayout(new GridLayout(2, false));
		
		table = createTable(SWT.VIRTUAL | SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		for(int i = 0; i < NUM_COLUMNS; i++) {
			createColumn(i);
		}
		table.addListener (SWT.SetData, new Listener() {
			public void handleEvent (Event event) {
				handleSetData(event);
			}
		});
		table.addListener(SWT.MenuDetect, menuDetectListener);
		
		table.setItemCount(ROW_COUNT);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		final Thread thread = new Thread() {
			@Override
			public void run() {
				runUpdateCycle();
			}
		};
		
		shell.setBounds(100, 50, 100, 900);
		
		thread.start();
		runEventLoop();
		
		doUpdate.set(false);
		try {
			thread.join();
		} catch (InterruptedException e) {
		}
	}

	protected Table createTable(int style) {
		table = new Table(shell, style);
		table.setHeaderVisible(true);
		return table;
	}
	
	protected void createColumn(int i) {
		final TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setText("Col" + i);
		tableColumn.setWidth(100);
	}

	protected void handleSetData(Event event) {
		TableItem item = (TableItem) event.item;
		int rowIndex = event.index;
		fillItem(item, rowIndex);
	}

	protected void fillItem(TableItem item, int index) {
		item.setText(0, "" + index + " " + TIMESTAMPSHORT_FORMAT.format(new Date()));
		for(int i = 1; i < NUM_COLUMNS; i++) {
			item.setText(i, "xxxcol " + i);
		}
	}

	private void clearItems() {
//		table.setRedraw(false);
		doClearItems();
		for(int i = 0; i < NUM_COLUMNS; i++) {
//			System.out.print(table.getColumn(i).getWidth() + " ");
		}
//		System.out.println();
		
//		table.setRedraw(true);
	}
	
	protected void doClearItems() {
		table.clearAll();
//		table.setItemCount(COUNT);
	}
	
	AtomicBoolean doUpdate = new AtomicBoolean(true);

	private void runUpdateCycle() {
		while(doUpdate.get()) {
			
			long time = System.currentTimeMillis();
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					if(!table.isDisposed())
						clearItems();
				}
			});
			System.out.println(System.currentTimeMillis() - time);
		}
	}
	
	private final Listener menuDetectListener = new Listener() {
		public void handleEvent(Event event) {
			final Point point = new Point(event.x, event.y);
			System.out.println("MenuDetect: start ");
			Menu menu = new Menu(table);
			
			MenuItem item = new MenuItem(menu, SWT.NONE);
			item.setText("A1");
			
			MenuItem item2 = new MenuItem(menu, SWT.NONE);
			item2.setText("A2");
			
			MenuItem item3 = new MenuItem(menu, SWT.NONE);
			item3.setText("A3");
			
			menu.setLocation(point);
			menu.setVisible(true);
		}
	};
	
}