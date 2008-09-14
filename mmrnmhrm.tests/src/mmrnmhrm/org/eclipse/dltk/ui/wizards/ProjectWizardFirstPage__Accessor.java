package mmrnmhrm.org.eclipse.dltk.ui.wizards;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import mmrnmhrm.tests.TestUtils;

import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;


public abstract class ProjectWizardFirstPage__Accessor {

	public static class NameGroup__Accessor {

		Object nameGroup;

		public NameGroup__Accessor(Object object) {
			nameGroup = object;
		}

		public void _setName(String name) throws NoSuchMethodException,
				IllegalAccessException, InvocationTargetException {
			Method method = nameGroup.getClass().getMethod("setName", String.class);
			method.setAccessible(true);
			method.invoke(nameGroup, name);
		}
	}

	public static NameGroup__Accessor access_fNameGroup(
			ProjectWizardFirstPage firstPage) throws SecurityException,
			IllegalArgumentException, IllegalAccessException {
		Field field = TestUtils.getFieldAcessibly(firstPage, "fNameGroup");
		return new NameGroup__Accessor(field.get(firstPage));
	}
	
}
