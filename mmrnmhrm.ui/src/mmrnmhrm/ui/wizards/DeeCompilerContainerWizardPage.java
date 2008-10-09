package mmrnmhrm.ui.wizards;

import mmrnmhrm.ui.preferences.DeeCompilersComboBlock;

import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterComboBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterContainerWizardPage;


public class DeeCompilerContainerWizardPage extends AbstractInterpreterContainerWizardPage {

	@Override
	protected AbstractInterpreterComboBlock getInterpreterBlock() {
		return new DeeCompilersComboBlock(null);
	}
}
