package descent.ui.actions;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.PlatformUI;

import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.internal.ui.ActiveJavaProjectLabelDecorator;

public class SetAsActiveProjectAction extends Action implements ISelectionChangedListener {
	
	private IJavaProject javaProject;
	
	@Override
	public String getText() {
		return "Set as Active Project"; //$NON-NLS-1$
	}
	
	@Override
	public void run() {
		if (javaProject != null) {
			IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
			model.setActiveProject(javaProject);
			
			PlatformUI.getWorkbench().getDecoratorManager().update(ActiveJavaProjectLabelDecorator.DECORATOR_ID);
		}
	}

	public void selectionChanged(SelectionChangedEvent event) {
		final ISelection selection = event.getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			if (sel.isEmpty()) {
				return;
			}
			Object firstElement = sel.getFirstElement();
			if (firstElement instanceof IAdaptable) {
				IAdaptable adapt = (IAdaptable) firstElement;
				IJavaProject project = (IJavaProject) adapt.getAdapter(IJavaProject.class);
				if (project != null) {
					this.javaProject = project;
					if (this.javaProject.getProject().isOpen()) {
						
						
						IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
						IJavaProject activeProject = model.getActiveProject();
						if (activeProject != null && activeProject.equals(javaProject)) {
							setChecked(true);
						}
						
						setChecked(false);
						setEnabled(true);
						return;
					}
				}
			}
		}
		
		setChecked(false);
		setEnabled(false);
	}

}
