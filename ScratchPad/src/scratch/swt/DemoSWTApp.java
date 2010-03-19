package scratch.swt;

import static melnorme.miscutil.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;


public class DemoSWTApp {
	
	protected Display display;
	protected Shell shell;

	public DemoSWTApp() {
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
		new DemoSWTApp().run(args);
	}

	protected void run(@SuppressWarnings("unused") String[] args) {
		
//		new SimpleWorkerThread("Blah", new Runnable(){
//			@Override
//			public void run() {
//				doubleSyncTest();
//			}
//
//		}).start();
		
//		Display.getDefault().asyncExec(new Runnable() {
//			public void run() {
//				MessageDialog.openConfirm(shell, "title", "Message");		
//			}
//		});
		
		final Composite composite = new Composite(shell, SWT.BORDER);
		composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_CYAN));
		composite.addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				final Menu menu = new Menu(shell);
				assertTrue(menu.isVisible() == false);
				final MenuItem menuItem1 = new MenuItem(menu, SWT.NONE);
				menuItem1.setText("Menu1");
				final MenuItem menuItem2 = new MenuItem(menu, SWT.NONE);
				menuItem2.setText("Menu2");
				final MenuItem menuItem3 = new MenuItem(menu, SWT.NONE);
				menuItem3.setText("Menu3");
				menu.setDefaultItem(menuItem2);
				
				menu.setVisible(true);
				
				menu.addDisposeListener(new DisposeListener() {
					@Override
					public void widgetDisposed(DisposeEvent e) {
						System.out.println();
					}
				});
			}
		});
		
		runEventLoop();
	}
	
	public static void doubleSyncTest() {
		System.out.println("doubleSyncTest start");
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				System.out.println("Outer sync start");
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						System.out.println("Inner sync");
					}
				});
				System.out.println("Outer sync END");
			}
		});
		System.out.println("doubleSyncTest END");
	}

}