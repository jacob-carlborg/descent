 package scratch.swt.performance;

import static melnorme.miscutil.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class Tree_Performance extends AbstractTableWidget_Performance {
	
	public static void main(String[] args) {
		new Tree_Performance().createAndRunApplication();
	}
	
	private Tree tree;


	protected Control createTable(int style) {
		tree = new Tree(shell, style);
		tree.setHeaderVisible(true);
		return tree;
	}
	
	@Override
	protected Control getControl() {
		return tree;
	}
	
	@Override
	protected void createWidget() {
		tree = (Tree) createTable(SWT.VIRTUAL | SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		for(int i = 0; i < COLUMN_COUNT; i++) {
			createColumn(i);
		}
		tree.addListener (SWT.SetData, new Listener() {
			public void handleEvent (Event event) {
				handleSetData(event);
			}
		});
	}
	
	protected void createColumn(int i) {
		final TreeColumn tableColumn = new TreeColumn(tree, SWT.NONE);
		tableColumn.setText(getColumnText(i));
		tableColumn.setWidth(COLUM_WIDTH1);
	}

	
	@Override
	protected void resetWidgetStructure() {
		tree.removeAll();
		tree.setItemCount(ROW_COUNT);
	}

	protected void handleSetData(Event event) {
		TreeItem item = (TreeItem) event.item;
		int index = tree.indexOf(item);
		int rowIx = event.index;
		assertTrue(index == rowIx);
		
		fillItem(item, rowIx);
	}
	
	protected void fillItem(TreeItem item, int rowIndex) {
		for(int colIndex = 0; colIndex < COLUMN_COUNT; colIndex++) {
			item.setText(colIndex, getCellText(rowIndex, colIndex));
		}
	}
	

	@Override
	protected void refreshWidgetCells() {
		tree.clearAll(true);
	}
	
	@Override
	protected void refreshWidgetCell(int rowIndex, int columIndex) {
		// Can only clear a whole row
		tree.clear(rowIndex, true);
	}


}