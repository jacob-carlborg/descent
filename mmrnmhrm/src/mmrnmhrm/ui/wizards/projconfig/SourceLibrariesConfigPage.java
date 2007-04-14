package mmrnmhrm.ui.wizards.projconfig;

import java.util.ArrayList;
import java.util.List;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeProject;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ITreeListAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.TreeListDialogField;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceSorter;

import util.tree.IElement;

public class SourceLibrariesConfigPage extends AbstractConfigPage {

	TreeListDialogField fSrcFoldersList;
	
	private final int IDX_ADD= 0;
	private final int IDX_ADD_LINK= 1;
	private final int IDX_EDIT= 3;
	private final int IDX_REMOVE= 4;

	
	public SourceLibrariesConfigPage() {
		fWorkspaceRoot = DeeCore.getWorkspaceRoot();
		
		SourceContainerAdapter adapter = new SourceContainerAdapter();

		String[] buttonLabels;
		buttonLabels = new String[] { 
			"&Add Folder...",
			"&Link Source...",
			/* separator */ null,
			"&Edit...", 
			"&Remove"
		};
		
		fSrcFoldersList= new TreeListDialogField(adapter, buttonLabels, new BPListLabelProvider());
		fSrcFoldersList.setDialogFieldListener(adapter);
		fSrcFoldersList.setLabelText("Source folders on build pat&h:"); 
		
		fSrcFoldersList.setRemoveButtonIndex(IDX_REMOVE);
		fSrcFoldersList.enableButton(IDX_EDIT, false);
		

	}
	
	@Override
	public void init(DeeProject project) {
		super.init(project);
	}
	
	@Override
	protected void createContents(Composite content) {
		LayoutUtil.doDefaultLayout(content, new DialogField[] { fSrcFoldersList}, true, SWT.DEFAULT, SWT.DEFAULT);
		LayoutUtil.setHorizontalGrabbing(fSrcFoldersList.getTreeControl(null));
	
		fSrcFoldersList.setButtonsMinWidth(converter.convertWidthInCharsToPixels(24));
	}
	
	

	private class SourceContainerAdapter implements ITreeListAdapter, IDialogFieldListener {
		
		// -------- IListAdapter --------
		public void customButtonPressed(TreeListDialogField field, int index) {
			if (index == IDX_ADD) {
				addEntry();
			} else if (index == IDX_ADD_LINK) {
				ContainerSelectionDialog dialog = 
					new ContainerSelectionDialog(getShell(), fDeeProject.getProject().getFolder("bin"), true, "MESSAGE");
				dialog.open();
			} else if (index == IDX_EDIT) {
				editEntry();
			} else if (index == IDX_REMOVE) {
				removeEntries();
			}
		}
		
		public void selectionChanged(TreeListDialogField field) {
			List selected= fSrcFoldersList.getSelectedElements();
			fSrcFoldersList.enableButton(IDX_EDIT, canEdit(selected));
			fSrcFoldersList.enableButton(IDX_REMOVE, canRemove(selected));
		}
		
		public void doubleClicked(TreeListDialogField field) {
			List selection= field.getSelectedElements();
			if (canEdit(selection)) {
				editEntry();
			}
		}
		
		public void keyPressed(TreeListDialogField field, KeyEvent event) {
			if (event.character == SWT.DEL && event.stateMask == 0) {
				List selection= field.getSelectedElements();
				if (canRemove(selection)) {
					removeEntries();
				}
			}
		}	

		public Object[] getChildren(TreeListDialogField field, Object element) {
			return IElement.NO_ELEMENTS;
		}

		public Object getParent(TreeListDialogField field, Object element) {
			return null;
		}

		public boolean hasChildren(TreeListDialogField field, Object element) {
			return false;
		}		
		
		// ---------- IDialogFieldListener --------
		public void dialogFieldChanged(DialogField field) {
		}
		
		private boolean canEdit(List selElements) {
			if (selElements.size() != 1) {
				return false;
			}
			return true;
		}


		private boolean canRemove(List selElements) {
			if (selElements.size() == 0) {
				return false;
			}
			return true;
		}
		
		private void editEntry() {
			List selElements= fSrcFoldersList.getSelectedElements();
			if (selElements.size() != 1) {
				return;
			}
			Object elem= selElements.get(0);
			if (fSrcFoldersList.getIndexOfElement(elem) != -1) {
				editElementEntry(elem);
			} 
		}
	}
	
	ProjectContainerSelectionDialog containerDialog ;

	private void addEntry() {
		containerDialog = new ProjectContainerSelectionDialog();
		containerDialog.dialog.setTitle("Choose a folder"); 
		containerDialog.dialog.setMessage("Choose a folder desc.");

		IContainer container = containerDialog.chooseContainer();
		if (container != null) {
			fSrcFoldersList.addElement(container);
		}
	}
	

	private void editElementEntry(Object elem) {
		// TODO
	}

	
	private void removeEntries() {
		List selElements= fSrcFoldersList.getSelectedElements();
		for (int i= selElements.size() - 1; i >= 0 ; i--) {
			Object elem= selElements.get(i);
			removeEntry(elem);
		}
	}

	private void removeEntry(Object elem) {
	}


	// -------- Output folder Controller --------
	protected class ProjectContainerSelectionDialog {
		public FolderSelectionDialog dialog;
		
		public ProjectContainerSelectionDialog() {
			Class[] acceptedClasses= new Class[] { IProject.class, IFolder.class };
			ISelectionStatusValidator validator= new TypedElementSelectionValidator(acceptedClasses, false);
			IProject[] allProjects= fWorkspaceRoot.getProjects();
			List<IProject> rejectedElements = 
				new ArrayList<IProject>(allProjects.length);
			IProject currProject= fDeeProject.getProject();
			for (int i= 0; i < allProjects.length; i++) {
				if (!allProjects[i].equals(currProject)) {
					rejectedElements.add(allProjects[i]);
				}
			}
			ViewerFilter filter= new TypedViewerFilter(acceptedClasses, rejectedElements.toArray());

			ILabelProvider lp= new WorkbenchLabelProvider();
			ITreeContentProvider cp= new WorkbenchContentProvider();
			
			dialog = new FolderSelectionDialog(getShell(), lp, cp);
			dialog.setTitle("Choose a folder"); 
			dialog.setValidator(validator);
			dialog.setMessage("Choose a folder desc."); 
			dialog.addFilter(filter);
			dialog.setInput(fWorkspaceRoot);
			dialog.setSorter(new ResourceSorter(ResourceSorter.NAME));

		}
		
		public IContainer chooseContainer() {
			if (dialog.open() == Window.OK) {
				return (IContainer)dialog.getFirstResult();
			}
			return null;
		}
	}
	
}
