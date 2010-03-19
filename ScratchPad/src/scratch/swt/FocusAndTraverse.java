package scratch.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/* 
 * In a traverse Tab event, setting event.doit = false, will make the widget receive '\t' as a verify event,
 * Instead, to disable it, event.doit should be true, but event.detail should be SWT.TRAVERSE_NONE;
 */
public class FocusAndTraverse {
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
		Composite compositeA = new Composite(shell, SWT.NONE);
		compositeA.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		compositeA.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createSample(compositeA);
		Composite compositeB = new Composite(shell, SWT.NONE);
		compositeB.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
		compositeB.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createSample(compositeB);
	}

	private static void createSample(Composite parent) {
		parent.setLayout(new GridLayout());
		final Text text = new Text(parent, SWT.BORDER 
//				| SWT.MULTI
				);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		layoutData.heightHint = 50;
		text.setLayoutData(layoutData);
		text.setText("text");
		
		//text.setSelection(1, 3);
		text.setSelection(3, 1);
		text.setSelection(1);

		final Text text2 = new Text(parent, SWT.BORDER);
		text2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		text2.setText("text2");

		
		final Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Sash sash = new Sash(parent, SWT.HORIZONTAL);
		sash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_MAGENTA));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		
		Listener printEventListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				System.out.println((event.doit ? "" : "X ") + eventTypeToString(event) + " " + event);
			}

		};
		text.addListener(SWT.FocusIn, printEventListener);
		text.addListener(SWT.FocusOut, printEventListener);
		text.addListener(SWT.Traverse, printEventListener);
		text.addListener(SWT.Selection, printEventListener);
		text.addListener(SWT.Verify, printEventListener);
		text2.addListener(SWT.FocusIn, printEventListener);
		text2.addListener(SWT.FocusOut, printEventListener);
		text2.addListener(SWT.Traverse, printEventListener);
		text2.addListener(SWT.Selection, printEventListener);
		text2.addListener(SWT.Verify, printEventListener);
		parent.addListener(SWT.Selection, printEventListener);
		parent.addListener(SWT.MouseDown, printEventListener);
		
		
		button.addListener(SWT.FocusIn, printEventListener);
		button.addListener(SWT.FocusOut, printEventListener);
		button.addListener(SWT.Traverse, printEventListener);
		button.addListener(SWT.Selection, printEventListener);
		
		composite.addListener(SWT.MouseHover, new Listener() {
			@Override
			public void handleEvent(Event event) {
				System.err.println(text.getSelection() + " " + text.getCaretPosition());
			}
		});

		
		text.addListener(SWT.Traverse,  new Listener() {
			@Override
			public void handleEvent(Event event) {
				if(event.detail == SWT.TRAVERSE_TAB_NEXT) {
		
					
//					text2.setSelection(0, text2.getText().length());
//					button.setFocus();
//					event.doit = false;
					event.detail = SWT.TRAVERSE_NONE;
					System.out.println(text2.getSelection());
				}
			}
		});
			
		text2.addListener(SWT.Traverse,  new Listener() {
			@Override
			public void handleEvent(Event event) {

				if(event.detail == SWT.TRAVERSE_RETURN) {
					event.detail = SWT.TRAVERSE_NONE;
					text2.traverse(SWT.TRAVERSE_TAB_NEXT);
				}
			}
		});
		
		System.out.println(text.hashCode() + " -- " + text2.hashCode());
		
		button.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				text.traverse(SWT.TRAVERSE_TAB_NEXT);
			}
		});
	}
	
	protected static String eventTypeToString(Event event) {
		switch (event.type) {
		case SWT.FocusIn: return "FocusIn";
		case SWT.FocusOut: return "FocusOut";
		case SWT.Selection: return "Selection";
		case SWT.MouseDown: return "MouseDown";
		case SWT.MouseUp: return "MouseUp";
		case SWT.Traverse: return "Traverse(" + traverseEventDetailToString(event.detail)+")";
		case SWT.Verify: return "Verify(" + event.text +"|"+event.character+"|"+event.keyCode+")"+event.widget.hashCode() + " ";
		default: return "-Other-";
		}
	}

	protected static String traverseEventDetailToString(int detail) {
		switch (detail) {
		case SWT.TRAVERSE_RETURN: return "RETURN";
		case SWT.TRAVERSE_TAB_NEXT: return "TAB_NEXT";
		case SWT.TRAVERSE_TAB_PREVIOUS: return "TAB_PREVIOUS";
		default: return "--Other--";
		}
	}
}
