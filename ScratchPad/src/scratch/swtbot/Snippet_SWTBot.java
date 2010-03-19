package scratch.swtbot;

import static melnorme.miscutil.Assert.assertTrue;

import java.lang.reflect.Method;

import melnorme.miscutil.ReflectionUtils;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;

import scratch.utils.NewUtils;
import scratch.utils.SWTDebugUtils;
import scratch.utils.SWTUtils;




public abstract class Snippet_SWTBot {
	
	protected static Display display;
	protected static Shell shell;

	public static void main(String[] args) {
		display = new Display();
		shell = new Shell(display);
		shell.setBounds(50, 50, 400, 400);
		shell.setLayout(new FillLayout());
		create(shell);
		runEventLoop();
	}
	
	protected static void runEventLoop() {
		shell.open();
		
		text.forceFocus();
		final SWTBotText botText = new SWTBotText(text);
		botText.typeText("H");

		final Method method = ReflectionUtils.getAvailableMethod(Keystrokes.class, "create", Character.TYPE);
		final KeyStroke[] keys = ReflectionUtils.uncheckedInvoke(null, method, (Character) 'C');
		botText.pressShortcut(keys);
		
		SWTUtils.runPendingUIEvents(display, shell);
		
		
		final Thread thread = new Thread(){
			@Override
			public void run(){
				int key = SWT.ARROW_RIGHT;
				String platform = SWT.getPlatform();
				if (platform.equals("carbon") || platform.equals("cocoa") ) {
	    			key = SWT.ARROW_DOWN;
				}
		        postEvent(SWT.MOD3, SWT.KeyDown);
		        postEvent(key, SWT.KeyDown);
		        postEvent(key, SWT.KeyUp);
		        postEvent(SWT.MOD3, SWT.KeyUp);
			}
		};
		thread.run();
		
		
		final int midleKeyCode = SWT.ARROW_RIGHT;
		assertTrue(display.post(keyEvent(SWT.KeyDown, SWT.NONE, SWT.SHIFT, '\0')));
//		display.wake();
		SWTUtils.runPendingUIEvents(display, shell);
		System.out.println("ShiftDownPressed");
		assertTrue(display.post(keyEvent(SWT.KeyDown, SWT.SHIFT, midleKeyCode, '\0')));
//		display.wake();
		SWTUtils.runPendingUIEvents(display, shell);
		System.out.println("Now doing keyUp's");
		assertTrue(display.post(keyEvent(SWT.KeyUp, SWT.SHIFT, midleKeyCode, '\0')));
//		display.wake();
		SWTUtils.runPendingUIEvents(display, shell);
		assertTrue(display.post(keyEvent(SWT.KeyUp, SWT.NONE, SWT.SHIFT, '\0')));
//		display.wake();
		SWTUtils.runPendingUIEvents(display, shell);
			
		
		botText.pressShortcut(NewUtils.array(
				KeyStroke.getInstance(SWT.SHIFT, KeyStroke.NO_KEY), 
				KeyStroke.getInstance(SWT.ARROW_DOWN)));
		
		
		
		SWTUtils.runPendingUIEvents(display, shell);
		
		botText.pressShortcut(SWT.SHIFT, 'x');
		botText.pressShortcut(SWT.NONE, 'z');
		botText.pressShortcut(SWT.SHIFT, SWT.ARROW_RIGHT, '\0');
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
	}
	
	public static Event keyEvent(int type, int modifierKeys, int keycode, char character) {
		Event event = new Event();
		event.type = type;
		event.stateMask = modifierKeys;
		event.keyCode = keycode;
		event.character = character;
		return event;
	}
	
	
	public static void postEvent(int keyCode, int type) {
	    Event event = new Event();
	    event.type = type;
	    event.keyCode = keyCode;
	    display.post(event);
	}

	

	private static void create(Composite parent) {
		new Button(parent, SWT.PUSH);
		
		text = new Text(parent, SWT.BORDER);
		text.setText("Foo");
		text.addListener(SWT.KeyDown, listener);
		text.addListener(SWT.KeyUp, listener);
	}
	
	
	protected static final Listener listener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			System.out.println("CellEditorControl: " + SWTDebugUtils.toStringKeyEvent(event));
			switch (event.keyCode) {
			case SWT.ARROW_DOWN:
			case SWT.ARROW_UP:
			case SWT.PAGE_UP:
			case SWT.PAGE_DOWN:
				if ((event.stateMask & SWT.SHIFT) != 0) {
					System.out.println("Got SHIFT");
				} else {
				}
			}
		}
	};
	private static Text text;

}