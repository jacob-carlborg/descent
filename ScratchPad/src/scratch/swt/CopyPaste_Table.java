package scratch.swt;

import static melnorme.miscutil.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class CopyPaste_Table {
	
	protected static Display display;
	protected static Shell shell;
	private Table table;
	
	public static void main(String[] args) {
		display = new Display();
		shell = new Shell(display);
		shell.setBounds(50, 50, 400, 400);
		shell.setLayout(new FillLayout());
		shell.setText(CopyPaste_Table.class.getSimpleName());
		new CopyPaste_Table().run();
	}
	
	public CopyPaste_Table() {
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
		
		table = new Table(shell, SWT.VIRTUAL | SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		
		table.setHeaderVisible(true);
		final TableColumn column2 = new TableColumn(table, SWT.NONE);
		TableColumn column1 = new TableColumn(table, SWT.NONE);
		column1.setWidth(100);
		column1.setMoveable(true);
		column2.setWidth(100);
		column2.setMoveable(true);
		
//		table.setBackgroundImage(new Image(display, "foo.jpg"));
		
		table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				table.deselectAll();
			}
		});
		
		for (int i = 0; i < 6; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(1, "Row " + i + " (BLAH) ");
			item.setText(0, "Foo " + i);
			if(i == 2 || i == 4) {
				item.setBackground(display.getSystemColor(SWT.COLOR_MAGENTA));
				assertTrue(item.getBackground(1).equals(display.getSystemColor(SWT.COLOR_MAGENTA)));
				
				item.setBackground(1, display.getSystemColor(SWT.COLOR_RED));
				assertTrue(item.getBackground(1).equals(display.getSystemColor(SWT.COLOR_RED)));
				
				item.setBackground(1, null);
				assertTrue(item.getBackground(1).equals(display.getSystemColor(SWT.COLOR_MAGENTA)));
			}
		}
		table.setSelection(1, 3);
		
		table.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event event) {
				System.out.println(event + " --- " + (int) event.keyCode);
			}
		});
		
		runEventLoop();
	}
	

}
