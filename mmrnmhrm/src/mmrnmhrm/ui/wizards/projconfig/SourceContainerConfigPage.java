package mmrnmhrm.ui.wizards.projconfig;

import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.DeeSourceFolder;
import mmrnmhrm.core.model.LangSourceFolder;
import mmrnmhrm.ui.ExceptionHandler;
import mmrnmhrm.ui.util.fields.IEditorFieldAction;
import mmrnmhrm.ui.util.fields.ListEditorField;
import mmrnmhrm.ui.util.fields.ListEditorField.IElementCommand;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ITreeListAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.TreeListDialogField;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import util.tree.IElement;

public class SourceContainerConfigPage extends AbstractConfigPage {


	private TreeListDialogField fSrcFoldersList;
	private StringButtonDialogField fOutputLocationField;
	

	private IPath fOutputLocationPath;	
	
	public SourceContainerConfigPage() {
		
		ListEditorField fSourceFoldersEditor =
			new ListEditorField();
		
		fSourceFoldersEditor.addCommand("&Add Folder...", new IEditorFieldAction() {
			public void execute(DialogField field) {
				createEntry();
			}
		});
		//fSourceFoldersEditor.addCommand("&Link Source...", TreeListEditorDialogField.COMMAND.ADD);
		fSourceFoldersEditor.addCommand(null, null);
		fSourceFoldersEditor.addEditCommand("&Edit...", new IElementCommand() {
			public void executeElementCommand(Object element) {
				editElementEntry(element);
			}
		});
		fSourceFoldersEditor.addRemoveCommand("&Remove", new IElementCommand() {
			public void executeElementCommand(Object element) {
				removeEntry(element);
			}
		});
		
		ILabelProvider lprovider = new BPListLabelProvider();
		ITreeListAdapter eAdapter = fSourceFoldersEditor.new TreeListEditorDialogFieldAdapter() {
			public Object[] getChildren(TreeListDialogField field, Object element) {
				return IElement.NO_ELEMENTS;
			}

			public Object getParent(TreeListDialogField field, Object element) {
				return null;
			}

			public boolean hasChildren(TreeListDialogField field, Object element) {
				return false;
			}
			
		}; 

		fSrcFoldersList = fSourceFoldersEditor.createTreeListEditor(eAdapter, lprovider);
		fSrcFoldersList.setLabelText("Source folders:"); 
		//fSrcFoldersList.setViewerSorter(new BPListElementSorter());
		
		
		OutputLocationAdapter outputLocationAdapter = new OutputLocationAdapter();
		
		fOutputLocationField = new StringButtonDialogField(outputLocationAdapter);
		fOutputLocationField.setButtonLabel("Bro&wse..."); 
		fOutputLocationField.setDialogFieldListener(outputLocationAdapter);
		fOutputLocationField.setLabelText("Defaul&t output folder (project-relative):"); 

	}
	

	public void init(DeeProject project) {
		super.init(project);
		updateView();
	}

	
	@Override
	protected void createContents(Composite content) {
		LayoutUtil.doDefaultLayout(content, new DialogField[] { fSrcFoldersList, fOutputLocationField}, true, SWT.DEFAULT, SWT.DEFAULT);
		LayoutUtil.setHorizontalGrabbing(fSrcFoldersList.getTreeControl(null));

		fSrcFoldersList.setButtonsMinWidth(converter.convertWidthInCharsToPixels(24));
	}


	private void updateView() {
		fSrcFoldersList.setElements(fDeeProject.getSourceFolders());
		fOutputLocationField.setTextWithoutUpdate(
				fDeeProject.getOutputDir().getProjectRelativePath().toString());
	}

	
	// -------- Source folders Controller --------
	
 

	private void createEntry() {
		ProjectFolderSelectionDialog containerDialog;
		containerDialog	= new ProjectFolderSelectionDialog();
		containerDialog.dialog.setTitle("Choose a folder"); 
		containerDialog.dialog.setMessage("Choose a folder desc.");

		IFolder container = containerDialog.chooseContainer();
		if (container != null) {
			addEntry(container);
		}
	}


	private void addEntry(IFolder container) {
		DeeSourceFolder sourceFolder = new DeeSourceFolder(container, fDeeProject);
		try {
			fSrcFoldersList.addElement(sourceFolder);
			fDeeProject.addSourceFolder(sourceFolder);
		} catch (CoreException e) {
			ExceptionHandler.handle(e, null, null);
		}
	}
	

	private void editElementEntry(Object elem) {
		MessageDialog.openInformation(getShell(), "Edit Source Folder", "TODO");
	}


	private void removeEntry(Object elem) {
		LangSourceFolder entry = (LangSourceFolder) elem;
		try {
			fDeeProject.removeSourceFolder(entry.folder);
			fSrcFoldersList.removeElement(entry);
		} catch (CoreException e) {
			ExceptionHandler.handle(e, null, null);
		}
	}


	// -------- Output folder Controller --------

	private class OutputLocationAdapter implements IStringButtonAdapter, IDialogFieldListener {
		

		public void changeControlPressed(DialogField field) {
			ProjectContainerSelectionDialog containerDialog;
			containerDialog	= new ProjectContainerSelectionDialog();
			containerDialog.dialog.setTitle("Folder Selection"); 
			containerDialog.dialog.setMessage("Choose the output location folder.");

			IResource initSelection = null;
			if (fOutputLocationPath != null) {
				initSelection = fWorkspaceRoot.findMember(fOutputLocationPath);
				containerDialog.dialog.setInitialSelection(initSelection);
			}

			IContainer container = containerDialog.chooseContainer();
			if (container != null) {
				fOutputLocationField.setText(container.getProjectRelativePath().toString());
			}
			
		}

		public void dialogFieldChanged(DialogField field) {
			validateAll();
		}

	}

	
	// -------- Validator --------
	private void validateAll() {
		
	}
}
