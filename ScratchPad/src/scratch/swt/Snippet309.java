package scratch.swt;

/*
 * Browser example snippet: Display different styles of the Search Text control
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 * 
 * @since 3.5
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Snippet309 {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell();
		shell.setLayout(new GridLayout(1, true));
		Text text;

		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				Text t = (Text) event.widget;
				String msg = t.getMessage();
				 {
					System.out.println("Default selection on " + msg);
				}
			}
		};

		text = new Text(shell, SWT.SEARCH);
		text.setMessage("search");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addListener(SWT.DefaultSelection, listener);

		text = new Text(shell, SWT.SEARCH );
		text.setMessage("search + cancel");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addListener(SWT.DefaultSelection, listener);

		text = new Text(shell, SWT.SEARCH );
		text.setMessage("search + icon");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addListener(SWT.DefaultSelection, listener);

		text = new Text(shell, SWT.SEARCH );
		text.setMessage("search + cancel + icon");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addListener(SWT.DefaultSelection, listener);

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();

	}
}
