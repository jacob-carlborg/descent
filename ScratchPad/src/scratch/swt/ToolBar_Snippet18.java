package scratch.swt;

/*
 * ToolBar example snippet: create a tool bar (text)
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class ToolBar_Snippet18 {

public static void main (String [] args) {
	Shell shell = new Shell ();
	ToolBar bar = new ToolBar (shell, SWT.BORDER);
	for (int i=0; i<8; i++) {
		ToolItem item = new ToolItem (bar, SWT.PUSH);
		item.setText ("Item " + i);
	}
	bar.pack ();
	shell.open ();
	Display display = shell.getDisplay ();
	while (!shell.isDisposed()) {
		if (!display.readAndDispatch ()) display.sleep ();
	}
	display.dispose ();
} 
}
