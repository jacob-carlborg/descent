package mmrnmhrm.ui.wizards.projconfig;

import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.DeeSourceFolder;
import mmrnmhrm.core.model.DeeSourceLib;
import mmrnmhrm.util.ui.LayoutUtil;
import mmrnmhrm.util.ui.fields.ElementContentProvider;
import mmrnmhrm.util.ui.fields.FieldUtil;
import mmrnmhrm.util.ui.fields.IElementCommand;
import mmrnmhrm.util.ui.fields.ListEditorField;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.TreeListDialogField;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class SourceLibrariesConfigPage extends AbstractConfigPage {

	TreeListDialogField fSrcLibrariesList;

	public SourceLibrariesConfigPage() {
		createSourceFoldersSection();
	}


	public void init(DeeProject project) {
		super.init(project);
		updateView();
	}

	
	@Override
	protected void createContents(Composite content) {
		FieldUtil.doDefaultLayout2(content, true, SWT.DEFAULT, SWT.DEFAULT,
				fSrcLibrariesList);
		LayoutUtil.enableHorizontalGrabbing(fSrcLibrariesList.getTreeControl(null));

		fSrcLibrariesList.setButtonsMinWidth(converter.convertWidthInCharsToPixels(24));
	}


	private void updateView() {
		fSrcLibrariesList.setElements(fDeeProject.getSourceLibraries());
	}

	
	// -------- Source folders section --------
 
	private void createSourceFoldersSection() {
		
		ListEditorField fSourceFoldersEditor = new ListEditorField();
		
		fSourceFoldersEditor.addElementOperation("&Add Library...", new IElementCommand() {
			public void executeCommand(Object element) {
				createLibraryEntry();
			}
		});
		fSourceFoldersEditor.addElementOperation("&Add External Library...", new IElementCommand() {
			public void executeCommand(Object element) {
				createExternalLibraryEntry();
			}
		});
		fSourceFoldersEditor.addElementOperation(null, null);

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
		fSrcLibrariesList = fSourceFoldersEditor.createTreeListEditor(lprovider);
		fSrcLibrariesList.setLabelText("Source libraries:"); 
		//fSrcFoldersList.setViewerSorter(new BPListElementSorter());
	}

	private void createLibraryEntry() {
		ProjectFolderSelectionDialog containerDialog;
		containerDialog	= new ProjectFolderSelectionDialog();
		containerDialog.dialog.setTitle("Choose a folder"); 
		containerDialog.dialog.setMessage("Choose a folder desc.");

		IFolder container = containerDialog.chooseContainer();
		if (container != null) {
			addEntry(container);
		}	
	}

	private void createExternalLibraryEntry() {
		// TODO
		MessageDialog.openInformation(getShell(), "Edit Source Folder", "TODO");
	}
	
	
	private void addEntry(IFolder container) {
		DeeSourceLib sourceFolder = new DeeSourceLib(fDeeProject, container);
		fSrcLibrariesList.addElement(sourceFolder);
		//fDeeProject.addSourceRoot(sourceFolder);
	}

	public void editElementEntry(Object element) {
		// TODO
		MessageDialog.openInformation(getShell(), "Edit Entry", "TODO");
	}

	public void removeEntry(Object element) {
		DeeSourceFolder entry = (DeeSourceFolder) element;
		fSrcLibrariesList.removeElement(entry);
		//fDeeProject.removeSourceRoot(entry);
	}
	


	// -------- Validator --------
	/*private void validateAll() {
		
	}*/

}
