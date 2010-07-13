package mmrnmhrm.org.eclipse.dltk.ui.wizards;

import java.lang.reflect.Field;

import mmrnmhrm.tests.TestUtils;

import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage.NameGroup;


public abstract class ProjectWizardFirstPage__Accessor {

	public static class NameGroup__Accessor {

		NameGroup nameGroup;

		public NameGroup__Accessor(NameGroup object) {
			nameGroup = object;
		}

		public void _setName(String name) {
			nameGroup.setName(name);
		}
	}

	public static NameGroup__Accessor access_fNameGroup(
			ProjectWizardFirstPage firstPage) throws SecurityException,
			IllegalArgumentException, IllegalAccessException {
		Field field = TestUtils.getFieldAcessibly(firstPage, "fNameGroup");
		return new NameGroup__Accessor((NameGroup) field.get(firstPage));
	}
	
}
