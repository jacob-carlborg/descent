package descent.internal.ui;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.JavaCore;

/*
 * Boldens the name of the active java project.
 */
public class ActiveJavaProjectLabelDecorator extends LabelProvider implements ILightweightLabelDecorator {
	
	public final static String DECORATOR_ID = "descent.ui.activeProjectDecorator"; //$NON-NLS-1$
	
	private static Font theFont;
	{
		Font font = JFaceResources.getDefaultFont();
		FontData fontData = font.getFontData()[0];
		theFont = new Font(Display.getCurrent(), fontData.getName(), fontData.getHeight(), fontData.getStyle() | SWT.BOLD);
	}

	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof IJavaProject) {
			IJavaProject javaProject = (IJavaProject) element;
			IJavaModel javaModel = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
			IJavaProject activeProject = javaModel.getActiveProject();
			if (activeProject != null && activeProject.equals(javaProject)) {
				decoration.setFont(theFont);
			}
		}
	}

}
