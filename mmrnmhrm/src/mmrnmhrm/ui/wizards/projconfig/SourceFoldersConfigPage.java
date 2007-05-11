package mmrnmhrm.ui.wizards.projconfig;

import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.DeeSourceFolder;
import mmrnmhrm.util.ui.LayoutUtil;
import mmrnmhrm.util.ui.fields.ElementContentProvider;
import mmrnmhrm.util.ui.fields.FieldUtil;
import mmrnmhrm.util.ui.fields.IElementCommand;
import mmrnmhrm.util.ui.fields.TreeListField;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.TreeListDialogField;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class SourceFoldersConfigPage extends AbstractConfigPage {

	protected TreeListDialogField fSrcFoldersList;
	protected StringButtonDialogField fOutputLocationField;
	
	IPath fOutputLocationPath;	
	
	public SourceFoldersConfigPage() {
		createSourceFoldersSection();
		createOutputLocationSection(); 
	}


	public void init(DeeProject project) {
		super.init(project);
		updateView();
	}

	
	@Override
	protected void createContents(Composite content) {
		FieldUtil.doDefaultLayout2(content, true, SWT.DEFAULT, SWT.DEFAULT,
				fSrcFoldersList, fOutputLocationField);
		LayoutUtil.enableHorizontalGrabbing(fSrcFoldersList.getTreeControl(null));

		fSrcFoldersList.setButtonsMinWidth(converter.convertWidthInCharsToPixels(24));
	}


	protected void updateView() {
		fSrcFoldersList.setElements(fDeeProject.getSourceFolders());
		// TODO MAKE TESTCASE FOR THIS
		fOutputLocationPath = fDeeProject.getOutputDir().getProjectRelativePath();
		fOutputLocationField.setTextWithoutUpdate(fOutputLocationPath.toString());
	}

	
	// -------- Source folders section --------
 
	private void createSourceFoldersSection() {
		
		TreeListField fSourceFoldersEditor = new TreeListField();
		
		fSourceFoldersEditor.addElementOperation("&Add Folder...", new IElementCommand() {
			public void executeCommand(Object element) {
				createEntry();
			}
		});
		fSourceFoldersEditor.addElementOperation(null, null);
		fSourceFoldersEditor.addEditOperation("&Edit...", new IElementCommand() {
			public void executeCommand(Object element) {
				editElementEntry(element);
			}
		});
		fSourceFoldersEditor.addRemoveOperation("&Remove", new IElementCommand() {
			public void executeCommand(Object element) {
				removeEntry(element);
			}
		});
		
		fSourceFoldersEditor.provider = new ElementContentProvider() {
			public boolean hasChildren(Object element) {
				return false;
			}
		}; 
		
		ILabelProvider lprovider = new BPListLabelProvider();
		fSrcFoldersList = fSourceFoldersEditor.createTreeList(lprovider);
		fSrcFoldersList.setLabelText("Source folders:"); 
		//fSrcFoldersList.setViewerSorter(new BPListElementSorter());
	}

	public void createEntry() {
		ProjectFolderSelectionDialog containerDialog;
		containerDialog	= new ProjectFolderSelectionDialog();
		containerDialog.dialog.setTitle("Choose a folder"); 
		containerDialog.dialog.setMessage("Choose a folder desc.");

		IFolder container = containerDialog.chooseContainer();
		if (container != null) {
			addEntry(container);
		}
	}
	
	protected void addEntry(IFolder container) {
		DeeSourceFolder sourceFolder = new DeeSourceFolder(container, fDeeProject);
		fSrcFoldersList.addElement(sourceFolder);
		//fDeeProject.addSourceRoot(sourceFolder);
	}

	protected void editElementEntry(Object element) {
		// TODO
		MessageDialog.openInformation(getShell(), "Edit Source Folder", "TODO");
	}

	protected void removeEntry(Object element) {
		DeeSourceFolder entry = (DeeSourceFolder) element;
		fSrcFoldersList.removeElement(entry);
		//fDeeProject.removeSourceRoot(entry);
	}

	


	// -------- Output location section --------

	private void createOutputLocationSection() {
		OutputLocationAdapter outputLocationAdapter = new OutputLocationAdapter();
		
		fOutputLocationField = new StringButtonDialogField(outputLocationAdapter);
		fOutputLocationField.setButtonLabel("Bro&wse..."); 
		fOutputLocationField.setDialogFieldListener(outputLocationAdapter);
		fOutputLocationField.setLabelText("Defaul&t output folder (project-relative):");
	}
	
	private class OutputLocationAdapter implements IStringButtonAdapter, IDialogFieldListener {

		public void changeControlPressed(DialogField field) {
			ProjectContainerSelectionDialog containerDialog;
			containerDialog	= new ProjectContainerSelectionDialog();
			containerDialog.dialog.setTitle("Folder Selection"); 
			containerDialog.dialog.setMessage("Choose the output location folder.");

			IResource initSelection = null;
			if (fOutputLocationPath != null) {
				initSelection = fDeeProject.getProject().findMember(fOutputLocationPath);
				containerDialog.dialog.setInitialSelection(initSelection);
			}

			IContainer container = containerDialog.chooseContainer();
			if (container != null) {
				fOutputLocationField.setText(container.getProjectRelativePath().toString());
				fOutputLocationPath = container.getProjectRelativePath();
			}
			
		}

		public void dialogFieldChanged(DialogField field) {
			IResource resource = fDeeProject.getProject().findMember(
					fOutputLocationField.getText());
			if(resource != null)
				fOutputLocationPath = resource.getProjectRelativePath();
			
			validateAll();
		}

	}

	
	// -------- Validator --------
	private void validateAll() {
		// TODO validate fOutputLocationPath
	}
}
