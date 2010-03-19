package scratch.swt.forms;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import scratch.swt.SWTApp;

/**
 * Measure listener called on pre-paint requests, or column pack.
 * When column exists, column width takes priority over measurer width
 */
public class TestSnippet_Form extends SWTApp {

	public static void main(String[] args) {
		new TestSnippet_Form().createAndRunApplication();
	}

	@Override
	public void createShellContents() {
		Composite parent = shell;
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = gridLayout.horizontalSpacing = 0;
		parent.setLayout(gridLayout);
				
		final FormToolkit toolkit = new FormToolkit(parent.getDisplay());
//		toolkit.adapt(parent);
		ScrolledForm viewForm = toolkit.createScrolledForm(parent);
		viewForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		viewForm.getToolBarManager().add(new ControlContribution("foo") {
			
			@Override
			protected Control createControl(Composite arg0) {
				return new Text(arg0, SWT.BORDER);
			}
		} );
		
		
		viewForm.getToolBarManager().add(new ControlContribution("foo") {
			
			@Override
			protected Control createControl(Composite parent) {
				return new StyledText(parent, SWT.BORDER);
			}
		} );
		
		viewForm.updateToolBar();

	}
}
