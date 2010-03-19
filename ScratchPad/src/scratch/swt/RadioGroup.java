package scratch.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class RadioGroup {
	
	protected Display display;
	protected Shell shell;

	public RadioGroup() {
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
		new RadioGroup().run();
	}

	protected void run() {
		shell.setLayout(new GridLayout(1, false));
		
		Button button = new Button(shell, SWT.RADIO);
		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		button.setText("Button1");
		
		Button button2 = new Button(shell, SWT.RADIO);
		button2.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		button2.setText("Button2");

		Button button3 = new Button(shell, SWT.RADIO);
		button3.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		button3.setText("Button3");
		
		Composite groupParent;
		
//		Group group = new Group(shell, SWT.NONE);
//		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
//		group.setLayout(new GridLayout(1, false));
//		group.setText("Group");
//		groupParent = group;
		
		groupParent = new Composite(shell, SWT.NONE);
		groupParent.setLayout(new GridLayout(1, false));
		groupParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Button buttonG2 = new Button(groupParent, SWT.RADIO);
		buttonG2.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		buttonG2.setText("Button2");

		Button buttonG3 = new Button(groupParent, SWT.RADIO);
		buttonG3.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		buttonG3.setText("Button3");

		runEventLoop();
	}
	
}