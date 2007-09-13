package mmrnmhrm.ui.launch;

import mmrnmhrm.core.model.DeeNature;

import org.eclipse.dltk.debug.ui.launchConfigurations.InterpreterTab;
import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterComboBlock;

public class RubyInterpreterTab extends InterpreterTab {
	
	@Override
	protected AbstractInterpreterComboBlock getInterpreterBlock() {
		return new RubyInterpreterComboBlock();
	}

	@Override
	protected String getNature() {
		return DeeNature.NATURE_ID;
	}
	
}
