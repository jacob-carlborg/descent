package scratch.swt;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public abstract class Text_VerifyModify {
	
	protected static Display display;
	protected static Shell shell;

	public static void main(String[] args) {
		display = new Display();
		shell = new Shell(display);
		shell.setBounds(50, 50, 400, 400);
		shell.setLayout(new FillLayout());
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
		shell.setLayout(null);
		
		final Listener verifyListener = new Listener() {
			@Override
			public void handleEvent (Event e) {
				System.out.println("Verify:" + e);
			}
		};
		final Listener modifyListener = new Listener() {
			@Override
			public void handleEvent (Event e) {
				System.out.println("Modify:" + e);
			}
		};
		
		final Listener traverseListener = new Listener() {
			@Override
			public void handleEvent (Event e) {
//				e.doit = true;
//				e.detail = SWT.TRAVERSE_NONE;
				System.out.println("Traverse:" + e);
			}
		};

		final Listener keyListener = KeyEvents_Snippet25.keyListener;
		
		final Text text = new Text(shell, SWT.BORDER);
		text.setBounds(10, 10, 200, 40);
		text.addListener(SWT.Modify, modifyListener);
		text.addListener(SWT.Verify, verifyListener);
		text.addListener(SWT.KeyDown, keyListener);
		text.addListener(SWT.KeyUp, keyListener);
		text.addListener(SWT.Traverse, traverseListener);
		
		
		SimpleContentProposalProvider proposalProvider = new SimpleContentProposalProvider(new String[]{"Foo", "Bar", "Baz", "Xpto"});
		ContentProposalAdapter proposalAdapter = new ContentProposalAdapter(text, new TextContentAdapter(), proposalProvider, null, null);
		proposalAdapter.isEnabled();
		proposalAdapter.setAutoActivationDelay(200);

		Text text2 = new Text(shell, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		text2.setBounds(10, 60, 200, 100);
		text2.addListener(SWT.Modify, modifyListener);
		text2.addListener(SWT.Verify, verifyListener);
		text2.addListener(SWT.KeyDown, keyListener);
		text2.addListener(SWT.KeyUp, keyListener);
		text2.addListener(SWT.Traverse, traverseListener);
		
		
		StyledText textViewer = new StyledText(shell, SWT.BORDER);
		textViewer.setBounds(10, 180, 200, 40);
		textViewer.addListener(SWT.Modify, modifyListener);
		textViewer.addListener(SWT.Verify, verifyListener);
		textViewer.addListener(SWT.KeyDown, keyListener);
		textViewer.addListener(SWT.KeyUp, keyListener);
		textViewer.addListener(SWT.Traverse, traverseListener);
		
		ContentProposalAdapter proposalAdapter2 = new ContentProposalAdapter(textViewer, new StyledTextContentAdapter(), proposalProvider, null, null);
		proposalAdapter2.isEnabled();
		proposalAdapter2.setAutoActivationDelay(200);
		
		
		Button button = new Button(shell, SWT.PUSH);
//		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		button.setBounds (10, 260, 100, 30);
		button.setText("Foo");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text.setText("Ba");
				text.setFocus();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				text.setText("foo");
			}
		});
		
	}

}