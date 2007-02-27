package mmrnmhrm.ui.wizards;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * TODO
 * @see org.eclipse.jdt.internal.ui.wizards.JavaProjectWizardFirstPage 
 */
public class DeeProjectWizardFirstPage extends WizardPage {

	private Text containerText;

	private Text fileText;
	
	protected DeeProjectWizardFirstPage(String pageName) {
		super(pageName);
		setTitle("New Dee Project");
		setDescription("Mmnmhrm: New Dee Project.");
	}
	

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		final Composite composite= new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout(new GridLayout(1, false), true));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText("&Container:");

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				//dialogChanged();
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//handleBrowse();
			}
		});
		label = new Label(container, SWT.NULL);
		label.setText("&File name:");

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);

		setControl(container);		
	}


	protected GridLayout initGridLayout(GridLayout layout, boolean margins) {
		/** XXX: JDT
		 * Initialize a grid layout with the default Dialog settings. */
		/*layout.horizontalSpacing= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		if (margins) {
			layout.marginWidth= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		} else {
			layout.marginWidth= 0;
			layout.marginHeight= 0;
		}*/
		return layout;
	}


	public void performFinish(IProgressMonitor monitor) {
		
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				MessageDialog.openInformation(getShell(),
						"Partitions",
						"Project Created");
			}
		});		
	}


}
