 package scratch.swt.performance;

import static melnorme.miscutil.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class Table_Performance extends AbstractTableWidget_Performance {
	
	public static void main(String[] args) {
		new Table_Performance().createAndRunApplication();
	}

	private Table table;
	
	protected Control createTable(int style) {
		table = new Table(shell, style);
		table.setHeaderVisible(true);
		return table;
	}
	
	@Override
	protected Control getControl() {
		return table;
	}

	
	@Override
	protected void createWidget() {
		table = (Table) createTable(SWT.VIRTUAL | SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		for(int i = 0; i < COLUMN_COUNT; i++) {
			createColumn(i);
		}
		table.addListener (SWT.SetData, new Listener() {
			public void handleEvent (Event event) {
				handleSetData(event);
			}
		});
	}
	
	protected void createColumn(int i) {
		final TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setText(getColumnText(i));
		tableColumn.setWidth(COLUM_WIDTH1);
	}

	@Override
	protected void resetWidgetStructure() {
		table.removeAll();
		table.setItemCount(ROW_COUNT);
	}
	
	protected void handleSetData(Event event) {
		TableItem item = (TableItem) event.item;
		int index = table.indexOf(item);
		int rowIx = event.index;
		assertTrue(index == rowIx);
		
		fillItem(item, rowIx);
	}
	
	protected void fillItem(TableItem item, int rowIndex) {
		for(int colIndex = 0; colIndex < COLUMN_COUNT; colIndex++) {
			item.setText(colIndex, getCellText(rowIndex, colIndex));
		}
	}
	
	@Override
	protected void refreshWidgetCells() {
		table.clearAll();
	}
	
	@Override
	protected void refreshWidgetCell(int rowIndex, int columIndex) {
		// Can only clear a whole row
		table.clear(rowIndex);
	}

}