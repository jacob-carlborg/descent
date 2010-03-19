package scratch.swt;

import static melnorme.miscutil.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;


public class Menu_MenuLifecycleManagement {
	
	protected Display display;
	protected Shell shell;
	private Composite compositeB;
	private Button button;
	private Menu menu;
	private Composite composite;

	public Menu_MenuLifecycleManagement() {
		display = new Display();
		shell = new Shell(display);
		shell.setBounds(50, 50, 400, 400);
		shell.setLayout(new FillLayout());
	}
	
	protected void runEventLoop() {
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
	}
	
	public static void main(String[] args) {
		new Menu_MenuLifecycleManagement().run(args);
	}

	protected void run(@SuppressWarnings("unused") String[] args) {
		
		compositeB = new Composite(shell, SWT.BORDER);
		compositeB.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_MAGENTA));
		
		Button button2 = new Button(compositeB, SWT.PUSH);
		button2.setText("Foo2");
		button2.setBounds(10, 10, 90, 30);

		button = new Button(compositeB, SWT.PUSH);
		button.setText("Foo");
		button.setBounds(10, 50, 90, 30);
		button.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				System.out.println(menu.isVisible());
			}
		});
		
		

		
		composite = new Composite(shell, SWT.BORDER);
		composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_CYAN));
		composite.addListener(SWT.MenuDetect,  new Listener() {
			@Override
			public void handleEvent(Event event) {
				System.out.println("Got event: " + event);
				int c = 0;
				try {
					while (c < 1) {
						c++;
						createMenu();
					}
				} finally {
					System.out.println(c);
				}
			}

		});
		
		runEventLoop();
	}
	
	private void createMenu() {
		menu = new Menu(shell);
		assertTrue(menu.isVisible() == false);
		final MenuItem menuItem1 = new MenuItem(menu, SWT.NONE);
		menuItem1.setText("Menu1");
		final MenuItem menuItem2 = new MenuItem(menu, SWT.NONE);
		menuItem2.setText("Menu2");
		final MenuItem menuItem3 = new MenuItem(menu, SWT.NONE);
		menuItem3.setText("Menu3");
		menu.setDefaultItem(menuItem2);
		
		menu.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				System.out.println("Disposed");
			}
		});

		composite.setMenu(menu);
//		button.setFocus();
//		asyncSetFocus(menu);
		
	}

	private void asyncSetFocus(@SuppressWarnings("unused") final Menu menu) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				shell.setFocus();
				button.setFocus();
			}
		});
	}
	
}