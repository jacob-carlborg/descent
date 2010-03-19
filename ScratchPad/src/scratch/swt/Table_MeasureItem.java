package scratch.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * Measure listener called on pre-paint requests, or column pack.
 * When column exists, column width takes priority over measurer width
 */
public class Table_MeasureItem extends SWTApp {

	public static void main(String[] args) {
		new Table_MeasureItem().createAndRunApplication();
	}

	@Override
	public void createShellContents() {
		final Table table = new Table(shell, SWT.NONE);

		table.setHeaderVisible(true);
		TableColumn column1 = new TableColumn(table, SWT.NONE);
		
		table.setLinesVisible(true);
		for (int i = 0; i < 5; i++) {
			new TableItem(table, SWT.NONE).setText("item " + i);
		}
		new TableItem(table, SWT.NONE).setText("item XXXXXXXXXXXX");
		
		table.addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				System.out.println("Handle Event." + event.width);
				int defaultCellWidth = event.width;
				event.height = event.gc.getFontMetrics().getHeight() * 2;
				event.width = defaultCellWidth * 2;
			}
		});
		
		
		column1.setWidth(10);
		column1.pack();
	}
}
