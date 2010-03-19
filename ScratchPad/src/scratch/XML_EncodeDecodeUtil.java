package scratch;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public abstract class XML_EncodeDecodeUtil {
	
	protected static Display display;
	protected static Shell shell;

	public static void main(String[] args) {
		display = new Display();
		shell = new Shell(display);
		shell.setBounds(50, 50, 400, 400);
		shell.setLayout(new GridLayout());
		create();
		runEventLoop();
	}
	
	protected static void runEventLoop() {
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
	}
	
	private static void create() {
		
		final Text text = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		
		Button button = new Button(shell, SWT.PUSH);
		button.setText("Encode");
		
		final Text text2 = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP);

		Button button2 = new Button(shell, SWT.PUSH);
		button2.setText("Decode");
		
		
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final String encodedText = encodeXML(text.getText());
				text2.setText(encodedText);
				text2.setFocus();
			}

		});
		button2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final String decodedText = decodeXML(text2.getText());
				text.setText(decodedText);
			}
		});
		

		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));		
		text2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		button2.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
	}

	private static String decodeXML(String escapedXML) {
		String decodedXML = escapedXML;
		decodedXML = decodedXML.replaceAll("&lt;", "<");
		decodedXML = decodedXML.replaceAll("&gt;", ">");
		decodedXML = decodedXML.replaceAll("&quot;", "\"");
		decodedXML = decodedXML.replaceAll("&apos;", "'");
		decodedXML = decodedXML.replaceAll("&amp;", "&");
		return decodedXML;
	}

	private static String encodeXML(String text) {
		String escapedXML = text;
		escapedXML = escapedXML.replaceAll("&", "&amp;");

		escapedXML = escapedXML.replaceAll("<", "&lt;");
		escapedXML = escapedXML.replaceAll(">", "&gt;");
		escapedXML = escapedXML.replaceAll("\"", "&quot;");
		escapedXML = escapedXML.replaceAll("'", "&apos;");
		return escapedXML;
	}

	
}