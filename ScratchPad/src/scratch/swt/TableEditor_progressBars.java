package scratch.swt;

/*
 * TableEditor example snippet: place a progress bar in a table
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class TableEditor_progressBars {
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout (new FillLayout ());
		Table table = new Table (shell, SWT.BORDER);
		table.setHeaderVisible (true);
		table.setLinesVisible(true);
		for (int i=0; i<2; i++) {
			new TableColumn (table, SWT.NONE);
		}
		table.getColumn (0).setText ("Task");
		table.getColumn (1).setText ("Progress");
		for (int i=0; i<40; i++) {
			TableItem item = new TableItem (table, SWT.NONE);
			item.setText ("Task " + i);
			if ( i % 5 == 0) {
				ProgressBar bar = new ProgressBar (table, SWT.NONE);
				bar.setSelection (i);
				TableEditor editor = new TableEditor (table);
				editor.grabHorizontal = editor.grabVertical = true;
				editor.setEditor (bar, item, 1);
			}
		}
		table.getColumn (0).pack ();
		table.getColumn (1).setWidth (128);
		shell.pack ();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}
