package scratch.swt.layout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import scratch.swt.SWTApp;

import scratch.utils.SWTUtils;

public class Test_BoundsTrim extends SWTApp {
	
	public static void main(String[] args) {
		new Test_BoundsTrim().createAndRunApplication();
	}

	
	@Override
	protected void createShellContents() {
		shell.setLayout(new FillLayout());
		
		Composite container = new Composite(shell, SWT.BORDER);
		container.setBackground(SWTUtils.getSystemColor(SWT.COLOR_GREEN));
		
		Composite childContainer = new Composite(container, SWT.BORDER);
		childContainer.setBounds(0, 0, 50, 50);
		childContainer.setBackground(SWTUtils.getSystemColor(SWT.COLOR_MAGENTA));
		
		Label label = new Label(container, SWT.NONE);
		label.setBounds(0, 60, 200, 40);

		shell.layout();
		label.setText(container.getClientArea().toString() + "\n"
				+ childContainer.getClientArea().toString());
	}
	
}
