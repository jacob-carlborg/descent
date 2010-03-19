package scratch.swt.performance;

import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.config.DefaultBodyConfig;
import net.sourceforge.nattable.config.SizeConfig;
import net.sourceforge.nattable.data.IDataProvider;
import net.sourceforge.nattable.model.DefaultNatTableModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;


public class NatTable_Performance extends AbstractTableWidget_Performance {
	
	public static void main(String[] args) {
		new NatTable_Performance().createAndRunApplication();
	}
	
	private NatTable natTable;
	private DefaultNatTableModel model;
	private DefaultBodyConfig bodyConfig;

	@Override
	protected void createWidget() {
		model = new DefaultNatTableModel();
		
		bodyConfig = new DefaultBodyConfig(new IDataProvider() {
			
			public int getColumnCount() {
				return COLUMN_COUNT;
			}

			public int getRowCount() {
				return ROW_COUNT;
			}

			public Object getValue(int row, int col) {
				return getCellText(row, col);
			}

		});
		model.setBodyConfig(bodyConfig);
		model.setSingleCellSelection(true);
		model.setMultipleSelection(true);

		// Row heights
		SizeConfig rowHeightConfig = bodyConfig.getRowHeightConfig();
		rowHeightConfig.setDefaultSize(15);
		rowHeightConfig.setDefaultResizable(true);
		rowHeightConfig.setIndexResizable(0, true);
		
		// Column Headers
		// Column widths
		bodyConfig.setColumnWidthConfig(new SizeConfig(COLUM_WIDTH1));
		SizeConfig columnWidthConfig = bodyConfig.getColumnWidthConfig();
		columnWidthConfig.setDefaultSize(100);
		columnWidthConfig.setInitialSize(0, 150);
		columnWidthConfig.setDefaultResizable(true);
		columnWidthConfig.setIndexResizable(1, false);
		
		
		final int NATTABLE_DEFAULT_STYLE = SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL | SWT.H_SCROLL;
		natTable = new NatTable(shell, NATTABLE_DEFAULT_STYLE, model
		);
		natTable.getSelectionModel().addSelection(1, 1);
	}

	@Override
	protected Control getControl() {
		return natTable;
	}

	@Override
	protected void refreshWidgetCell(int rowIndex, int columIndex) {
		natTable.redrawUpdatedBodyRow(rowIndex, rowIndex);
	}

	@Override
	protected void refreshWidgetCells() {
		natTable.redraw();
	}

	@Override
	protected void resetWidgetStructure() {
		natTable.redraw();
	}
	
}