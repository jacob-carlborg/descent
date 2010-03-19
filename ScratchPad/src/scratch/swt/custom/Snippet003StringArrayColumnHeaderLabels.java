package scratch.swt.custom;

import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.config.DefaultColumnHeaderConfig;
import net.sourceforge.nattable.data.ColumnHeaderLabelProvider;
import net.sourceforge.nattable.data.IColumnHeaderLabelProvider;
import net.sourceforge.nattable.model.DefaultNatTableModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Specify column header labels by a string array.
 */
public class Snippet003StringArrayColumnHeaderLabels {

	public static void main(String args[]) {
		new Snippet003StringArrayColumnHeaderLabels();
	}
	
	private Snippet003StringArrayColumnHeaderLabels() {
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
		DefaultNatTableModel model = new DefaultNatTableModel();
		
		// Column Headers
		String[] columnHeaderLabels = new String[] {
				"This",
				"That",
				"Other"
		};
		IColumnHeaderLabelProvider columnHeaderLabelProvider = new ColumnHeaderLabelProvider(columnHeaderLabels);
		model.setColumnHeaderConfig(new DefaultColumnHeaderConfig(columnHeaderLabelProvider));
		
		// NatTable
		new NatTable(
				parent,
				SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL | SWT.H_SCROLL,
				model
		);
	}
	
}
