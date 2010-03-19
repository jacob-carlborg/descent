package scratch.swtbot;

/*
 * UI Automation (for testing tools) snippet: post key events to simulate
 * moving the I-beam to the end of a text control
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

public class Snippet304 {
    static Display display = null; 

    public static void main(String[] args) {
        display = new Display();
        Shell shell = new Shell(display);

        shell.setLayout(new GridLayout());
        Button button = new Button(shell, SWT.NONE);
        button.setText("ASDFA");
//        Text text = new Text(shell, SWT.MULTI | SWT.BORDER);
//        text.setText("< cursor was there\na\nmulti\nline\ntext\nnow it's here >");
//
        final KeyListener listener = new KeyListener() {
            public void keyPressed(KeyEvent e) {
                System.out.println("KeyDown " + e);
            }
            public void keyReleased(KeyEvent e) {
                System.out.println("KeyUp   " + e);
            }
        };
//		text.addKeyListener(listener);
		button.addKeyListener(listener);
		button.forceFocus();
		
        shell.pack();
        shell.open();

        /*
        * Simulate the (platform specific) key sequence
        * to move the I-beam to the end of a text control.
        */
    	new Thread(){
    		@Override
			public void run(){
    			int key = SWT.END;
    			String platform = SWT.getPlatform();
    			if (platform.equals("carbon") || platform.equals("cocoa") ) {
        			key = SWT.ARROW_DOWN;
    			}
    	        postEvent(SWT.MOD2, SWT.KeyDown);
    	        postEvent(key, SWT.KeyDown);
    	        postEvent(key, SWT.KeyUp);
    	        postEvent(SWT.MOD2, SWT.KeyUp);
    		}
    	}.start();
        
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    public static void postEvent(int keyCode, int type) {
        Event event = new Event();
        event.type = type;
        event.keyCode = keyCode;
        display.post(event);
    }
}