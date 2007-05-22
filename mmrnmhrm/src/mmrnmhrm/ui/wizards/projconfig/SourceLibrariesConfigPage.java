package mmrnmhrm.ui.wizards.projconfig;

import melnorme.util.ui.LayoutUtil;
import melnorme.util.ui.fields.FieldUtil;
import melnorme.util.ui.fields.IElementCommand;
import melnorme.util.ui.fields.TreeListEditorField;
import melnorme.util.ui.fields.TreeListEditorField.TreeListContentProvider;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.DeeSourceFolder;
import mmrnmhrm.core.model.DeeSourceLib;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.TreeListDialogField;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
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
		
		TreeListEditorField fSourceFoldersEditor = new TreeListEditorField();
		
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
		
		ITreeContentProvider cprovider = new TreeListContentProvider() {
			public boolean hasChildren(Object element) {
				return false;
			}
		};
		ILabelProvider lprovider = new SPListLabelProvider();
		
		fSrcLibrariesList = fSourceFoldersEditor.createTreeList(cprovider, lprovider);
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
