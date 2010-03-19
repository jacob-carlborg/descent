package scratch.swt;

/*
 * ToolBar example snippet: place a combo box in a tool bar
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class ToolBar_Snippet58 {

public static void main (String [] args) {
	Display display = new Display ();
	Shell shell = new Shell (display);
	ToolBar bar = new ToolBar (shell, SWT.BORDER);
	for (int i=0; i<4; i++) {
		ToolItem item = new ToolItem (bar, 0);
		item.setText ("Item " + i);
	}
	ToolItem sep = new ToolItem (bar, SWT.SEPARATOR);
	int start = bar.getItemCount ();
	for (int i=start; i<start+4; i++) {
		ToolItem item = new ToolItem (bar, 0);
		item.setText ("Item " + i);
	}
	Combo combo = new Combo (bar, SWT.READ_ONLY);
	for (int i=0; i<4; i++) {
		combo.add ("Item " + i);
	}
	combo.pack ();
	sep.setWidth (combo.getSize ().x);
	sep.setControl (combo);
	bar.pack ();
	shell.pack ();
	shell.open ();
	while (!shell.isDisposed ()) {
		if (!display.readAndDispatch ()) display.sleep ();
	}
	display.dispose ();
}
} 
