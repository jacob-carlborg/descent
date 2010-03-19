package scratch.swt.custom;

import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.config.DefaultBodyConfig;
import net.sourceforge.nattable.config.IBodyConfig;
import net.sourceforge.nattable.config.SizeConfig;
import net.sourceforge.nattable.data.IDataProvider;
import net.sourceforge.nattable.model.DefaultNatTableModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Shows the minimal configuration needed to construct a NatTable instance. 
 */
public class NatTable_Test {

	public static void main(String args[]) {
		new NatTable_Test();
	}
	
	private NatTable_Test() {
		try {
			Display display = Display.getDefault();
			Shell shell = new Shell(display, SWT.SHELL_TRIM);
			shell.setLayout(new FillLayout());
			
			setupNatTable(shell);
			
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setupNatTable(Composite parent) {
		final DefaultNatTableModel model = new DefaultNatTableModel();
		
		IBodyConfig bodyConfig = new DefaultBodyConfig(new IDataProvider() {
			
			public int getColumnCount() {
				return 100;
			}

			public int getRowCount() {
				return 100;
			}

			public Object getValue(int row, int col) {
				if(col == 5) {
					return "" + row + " -ASDFASdfASDFASdFASDFSD asdfasdf asdfasdfasd fasdfasdFSDFSDF- " +  col;
				} else {
					return "" + row + " -- " +  col;
				}
			}

		});
		model.setBodyConfig(bodyConfig);
		model.setSingleCellSelection(true);
		model.setMultipleSelection(true);
		model.setSortingEnabled(true);

		// Column widths
		SizeConfig columnWidthConfig = model.getBodyConfig().getColumnWidthConfig();
		columnWidthConfig.setDefaultSize(100);
		columnWidthConfig.setInitialSize(0, 150);
		columnWidthConfig.setDefaultResizable(true);
		columnWidthConfig.setIndexResizable(1, false);

		
		// NatTable
		final NatTable natTable = new NatTable(
				parent,
				SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL | SWT.H_SCROLL,
				model
		);
		
//		natTable.getNatTableModel().setFreezeColumnCount(3);
//		natTable.reset();
//		natTable.updateResize();
		
		natTable.toString();
	}
	
}
