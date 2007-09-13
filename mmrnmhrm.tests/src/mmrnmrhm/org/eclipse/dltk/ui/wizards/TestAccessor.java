package mmrnmrhm.org.eclipse.dltk.ui.wizards;

import mmrnmrhm.org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage_;
import mmrnmrhm.org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage_.NameGroup;

import org.eclipse.swt.widgets.Composite;


public abstract class TestAccessor extends ProjectWizardFirstPage_ {

	public static abstract class TestAccessor_NameGroup extends NameGroup {
		public TestAccessor_NameGroup(Composite composite, String initialName) {
			((ProjectWizardFirstPage_)null).super(composite, initialName);
		}
		public static void _setName(ProjectWizardFirstPage_ firstPage, String name) {
			_fNameGroup(firstPage).setName(name);
		}
	}
	
	protected static NameGroup _fNameGroup(ProjectWizardFirstPage_ firstPage) {
		return firstPage.fNameGroup;
	}
	
}
