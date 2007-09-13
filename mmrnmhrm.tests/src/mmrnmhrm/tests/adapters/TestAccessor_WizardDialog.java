package mmrnmhrm.tests.adapters;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class TestAccessor_WizardDialog extends WizardDialog {
    public TestAccessor_WizardDialog(Shell parentShell, IWizard newWizard) {
    	super(parentShell, newWizard);
    }
    
    // make buttons public

    @Override
    public void nextPressed() {
    	super.nextPressed();
    }
    @Override
    public void backPressed() {
    	super.backPressed();
    }
    
    @Override
    public void finishPressed() {
    	super.finishPressed();
    }
    
    @Override
    public void cancelPressed() {
    	super.cancelPressed();
    }
}
