package mmrnmhrm.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * DeeProjectWizardFirstPage
 */
public class DeeProjectWizardSecondPage extends WizardPage {


	
	protected DeeProjectWizardSecondPage(String pageName) {
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

		
		Button button = new Button(composite, SWT.RADIO);
		button.setText("BUTTON");

		Button button2 = new Button(composite, SWT.RADIO);
		button2.setText("BUTTON2");

		setControl(composite);		
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


}
