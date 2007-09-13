package mmrnmhrm.ui.launch;

import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterComboBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterContainerWizardPage;


public class DeeInterpreterContainerWizardPage extends AbstractInterpreterContainerWizardPage {

	@Override
	protected AbstractInterpreterComboBlock getInterpreterBlock() {
		return new RubyInterpreterComboBlock();
	}
}
