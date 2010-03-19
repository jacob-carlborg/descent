package scratch.swt.custom;
import java.util.ArrayList;

import net.sourceforge.nattable.GridRegionEnum;
import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.config.DefaultBodyConfig;
import net.sourceforge.nattable.config.DefaultColumnHeaderConfig;
import net.sourceforge.nattable.data.ColumnHeaderLabelProvider;
import net.sourceforge.nattable.data.IDataProvider;
import net.sourceforge.nattable.event.matcher.MouseEventMatcher;
import net.sourceforge.nattable.model.DefaultNatTableModel;
import net.sourceforge.nattable.renderer.ColumnGroupHeaderRenderer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class Snippet010ColumnGroupTest extends Shell {

	public Snippet010ColumnGroupTest(Display display, int style) {
		super(display, style);
		createContents();
		final GridLayout gridLayout = new GridLayout();
		setLayout(gridLayout);
	}
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	private void createContents() {
		setText("Column Group Test");
		setSize(500, 375);

		final Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new FillLayout());

		ArrayList<Integer> columnGroup1 = new ArrayList<Integer>();
		columnGroup1.add(new Integer(0)); 
		columnGroup1.add(new Integer(2));
		columnGroup1.add(new Integer(6));
		ArrayList<Integer> columnGroup2 = new ArrayList<Integer>();
		columnGroup2.add(new Integer(4));
		columnGroup2.add(new Integer(3));		
		columnGroup2.add(new Integer(1));
		ArrayList<Integer> columnGroup3 = new ArrayList<Integer>();
		columnGroup3.add(new Integer(14));
		columnGroup3.add(new Integer(9));		
		columnGroup3.add(new Integer(12));
		
		final String[] labels = new String[] { "Column 0", "Column 1", "Column 2", "Column 3", "Column 4", 
				"Column 5", "Column 6", "Column 7", "Column 8", "Column 9", "Column 10", "Column 11", "Column 12", 
				"Column 13", "Column 14", "Column 15", "Column 16", "Column 17", "Column 18", "Column 19", "Column 20" };
		final ColumnHeaderLabelProvider labelProvider = new ColumnHeaderLabelProvider(labels);
		
		DefaultColumnHeaderConfig columnHeaderConfig = new DefaultColumnHeaderConfig(labelProvider);
		columnHeaderConfig.getColumnHeaderRowHeightConfig().setDefaultSize(20);
		DefaultNatTableModel natModel = new DefaultNatTableModel();
		natModel.setColumnHeaderConfig(columnHeaderConfig);
		natModel.setBodyConfig(new DefaultBodyConfig(new IDataProvider() {
			
			public int getColumnCount() {
				return labels.length;
			}

			public int getRowCount() {
				return 10;
			}

			public Object getValue(int row, int col) {
				return "Row " + row + ", Col " + col;
			}

		}));
		natModel.setFullRowSelection(true);
		natModel.setSortingEnabled(true);
		natModel.setEnableMoveColumn(true);
		natModel.getBodyConfig().getColumnWidthConfig().setDefaultResizable(true);

		NatTable natTable = new NatTable(composite, SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL | SWT.H_SCROLL, natModel);
		natTable.addColumnGroup("Column Group 1", columnGroup1, false); 
		natTable.addColumnGroup("Column Group 2", columnGroup2, true); 
		natTable.addColumnGroup("Column Group 3", columnGroup3, false); 
//		natTable.getColumnGroupSupport().setEnableAddColumns(false);
//		natTable.getColumnGroupSupport().setEnableColumnRemoval(false);
//		natTable.getColumnGroupSupport().setEnableReorderColumnGroup(false);
		
		natTable.getEventBindingSupport().registerMouseDownBinding(new MouseEventMatcher(SWT.NONE, GridRegionEnum.COLUMN_HEADER.toString(), 3), new PopupMenuAction(natTable.getShell()));
		natTable.getEventBindingSupport().registerMouseDownBinding(new MouseEventMatcher(SWT.NONE, GridRegionEnum.CORNER.toString(), 3), new PopupMenuAction(natTable.getShell()));
		ColumnGroupHeaderRenderer groupHeaderRenderer = new ColumnGroupHeaderRenderer(labelProvider, natTable.getColumnGroupSupport());
		columnHeaderConfig.setCellRenderer(groupHeaderRenderer);
		columnHeaderConfig.setColumnHeaderRowCount(2);

	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Display display = Display.getDefault();
			Snippet010ColumnGroupTest shell = new Snippet010ColumnGroupTest(display, SWT.SHELL_TRIM);
			shell.setLayout(new GridLayout());
			shell.open();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (	Exception e) {
			e.printStackTrace();

		}
	}
}
