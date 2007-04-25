package mmrnmhrm.ui.wizards.projconfig;

import java.util.List;

import mmrnmhrm.core.LangCore;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.util.ui.RowComposite;
import mmrnmhrm.util.ui.fields.FolderSelectionDialog;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.util.PixelConverter;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceSorter;

import util.ListUtil;

public abstract class AbstractConfigPage {

	protected DeeProject fDeeProject;
	protected IWorkspaceRoot fWorkspaceRoot;

	protected Control fSWTControl;
	protected PixelConverter converter;
	


	public AbstractConfigPage() {
		super();
	}

	public void init(DeeProject project) {
		this.fDeeProject = project;
		this.fWorkspaceRoot = LangCore.getWorkspaceRoot();
	}

	public Control getControl(Composite parent) {
		converter = new PixelConverter(parent);
		Composite content = new RowComposite(parent, true);
		fSWTControl = content;
		createContents(content);
		return content;
	}

	protected abstract void createContents(Composite content) ;

	protected Shell getShell() {
		if (fSWTControl != null) {
			return fSWTControl.getShell();
		}
		return JavaPlugin.getActiveWorkbenchShell();
	}

	protected class ProjectFolderSelectionDialog extends ProjectContainerSelectionDialog {
		public ProjectFolderSelectionDialog() {

			Class[] visibleClasses= new Class[] { IProject.class, IFolder.class };
			Class[] acceptedClasses= new Class[] { IFolder.class };
			
			IProject[] allProjects= fWorkspaceRoot.getProjects();
			List<IProject> rejectedElements 
				= ListUtil.copyExcept(allProjects, fDeeProject.getProject());
			
			ViewerFilter filter = new TypedViewerFilter(visibleClasses, rejectedElements.toArray());
			ISelectionStatusValidator validator= new TypedElementSelectionValidator(acceptedClasses, false);

			dialog = getDefaultFolderSelectionDialog();
			dialog.setTitle("Choose a folder"); 
			dialog.setMessage("Choose a folder desc."); 
			dialog.setValidator(validator);
			dialog.addFilter(filter);
		}
		
		public IFolder chooseContainer() {
			if (dialog.open() == Window.OK) {
				return (IFolder) dialog.getFirstResult();
			}
			return null;
		}
	}

	protected class ProjectContainerSelectionDialog {
		public FolderSelectionDialog dialog;
		
		public ProjectContainerSelectionDialog() {
			Class[] acceptedClasses= new Class[] { IProject.class, IFolder.class };
			
			IProject[] allProjects= fWorkspaceRoot.getProjects();
			List<IProject> rejectedElements 
				= ListUtil.copyExcept(allProjects, fDeeProject.getProject());
			
			ViewerFilter filter = new TypedViewerFilter(acceptedClasses, rejectedElements.toArray());
			ISelectionStatusValidator validator= new TypedElementSelectionValidator(acceptedClasses, false);

			dialog = getDefaultFolderSelectionDialog();
			dialog.setTitle("Choose a folder"); 
			dialog.setMessage("Choose a folder desc."); 
			dialog.setValidator(validator);
			dialog.addFilter(filter);
		}
		
		protected FolderSelectionDialog getDefaultFolderSelectionDialog() {
			ILabelProvider lp= new WorkbenchLabelProvider();
			ITreeContentProvider cp= new WorkbenchContentProvider();
			
			FolderSelectionDialog dialog;
			dialog = new FolderSelectionDialog(getShell(), lp, cp);
			dialog.setSorter(new ResourceSorter(ResourceSorter.NAME));
			dialog.setInput(fWorkspaceRoot);
			return dialog;
		}
		
		public IContainer chooseContainer() {
			if (dialog.open() == Window.OK) {
				return (IContainer)dialog.getFirstResult();
			}
			return null;
		}
	}

}