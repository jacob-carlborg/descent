package mmrnmhrm.ui.wizards;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import mmrnmhrm.ui.ActualPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;

public abstract class LangProjectWizardFirstPage extends WizardPage {

	/**
	 * Request a project name. Fires an event whenever the text field is
	 * changed, regardless of its content.
	 */
	protected final class NameGroup extends Observable implements IDialogFieldListener {

		protected final StringDialogField fNameField;

		public NameGroup(Composite composite, String initialName) {
			final Composite nameComposite= new Composite(composite, SWT.NONE);
			nameComposite.setFont(composite.getFont());
			nameComposite.setLayout(initGridLayout(new GridLayout(2, false), false));
			nameComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			// text field for project name
			fNameField= new StringDialogField();
			fNameField.setLabelText(DeeNewWizardMessages.LangNewProject_Page1_NameGroup_label); 
			fNameField.setDialogFieldListener(this);

			setName(initialName);

			fNameField.doFillIntoGrid(nameComposite, 2);
			LayoutUtil.setHorizontalGrabbing(fNameField.getTextControl(null));
		}
		
		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}

		public String getName() {
			return fNameField.getText().trim();
		}

		public void postSetFocus() {
			fNameField.postSetFocusOnDialogField(getShell().getDisplay());
		}
		
		public void setName(String name) {
			fNameField.setText(name);
		}

		/** {@inheritDoc} */
		public void dialogFieldChanged(DialogField field) {
			fireEvent();
		}
		
	}
	
	/**
	 * Request a location. Fires an event whenever the checkbox or the location
	 * field is changed, regardless of whether the change originates from the
	 * user or has been invoked programmatically.
	 */
	protected final class LocationGroup extends Observable implements Observer, IStringButtonAdapter, IDialogFieldListener {

		protected final SelectionButtonDialogField fWorkspaceRadio;
		protected final SelectionButtonDialogField fExternalRadio;
		protected final StringButtonDialogField fLocation;
		
		private String fPreviousExternalLocation;
		
		private static final String DIALOGSTORE_LAST_EXTERNAL_LOC= JavaUI.ID_PLUGIN + ".last.external.project"; //$NON-NLS-1$

		public LocationGroup(Composite composite) {

			final int numColumns= 3;

			final Group group= new Group(composite, SWT.NONE);
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			group.setLayout(initGridLayout(new GridLayout(numColumns, false), true));
			group.setText(DeeNewWizardMessages.LangNewProject_Page1_LocationGroup_title); 

			fWorkspaceRadio= new SelectionButtonDialogField(SWT.RADIO);
			fWorkspaceRadio.setDialogFieldListener(this);
			fWorkspaceRadio.setLabelText(DeeNewWizardMessages.LangNewProject_Page1_LocationGroup_workspace_desc); 

			fExternalRadio= new SelectionButtonDialogField(SWT.RADIO);
			fExternalRadio.setLabelText(DeeNewWizardMessages.LangNewProject_Page1_LocationGroup_external_desc); 

			fLocation= new StringButtonDialogField(this);
			fLocation.setDialogFieldListener(this);
			fLocation.setLabelText(DeeNewWizardMessages.LangNewProject_Page1_LocationGroup_locationLabel_desc); 
			fLocation.setButtonLabel(DeeNewWizardMessages.LangNewProject_Page1_LocationGroup_browseButton_desc); 
			fExternalRadio.attachDialogField(fLocation);
			
			fWorkspaceRadio.setSelection(true);
			fExternalRadio.setSelection(false);
			
			fPreviousExternalLocation= ""; //$NON-NLS-1$

			fWorkspaceRadio.doFillIntoGrid(group, numColumns);
			fExternalRadio.doFillIntoGrid(group, numColumns);
			fLocation.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fLocation.getTextControl(null));
		}
				
		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}

		protected String getDefaultPath(String name) {
			final IPath path= Platform.getLocation().append(name);
			return path.toOSString();
		}

		/** {@inheritDoc} */
		public void update(Observable o, Object arg) {
			if (isInWorkspace()) {
				fLocation.setText(getDefaultPath(fNameGroup.getName()));
			}
			fireEvent();
		}

		public IPath getLocation() {
			if (isInWorkspace()) {
				return Platform.getLocation();
			}
			return Path.fromOSString(fLocation.getText().trim());
		}

		public boolean isInWorkspace() {
			return fWorkspaceRadio.isSelected();
		}

		/** {@inheritDoc} */
		public void changeControlPressed(DialogField field) {
			final DirectoryDialog dialog= new DirectoryDialog(getShell());
			dialog.setMessage(DeeNewWizardMessages.LangNewProject_Page1_directory_message); 
			String directoryName = fLocation.getText().trim();
			if (directoryName.length() == 0) {
				String prevLocation= ActualPlugin.getInstance().getDialogSettings().get(DIALOGSTORE_LAST_EXTERNAL_LOC);
				if (prevLocation != null) {
					directoryName= prevLocation;
				}
			}
		
			if (directoryName.length() > 0) {
				final File path = new File(directoryName);
				if (path.exists())
					dialog.setFilterPath(directoryName);
			}
			final String selectedDirectory = dialog.open();
			if (selectedDirectory != null) {
				fLocation.setText(selectedDirectory);
				ActualPlugin.getInstance().getDialogSettings().put(DIALOGSTORE_LAST_EXTERNAL_LOC, selectedDirectory);
			}
		}

		/** {@inheritDoc} */
		public void dialogFieldChanged(DialogField field) {
			if (field == fWorkspaceRadio) {
				final boolean checked= fWorkspaceRadio.isSelected();
				if (checked) {
					fPreviousExternalLocation= fLocation.getText();
					fLocation.setText(getDefaultPath(fNameGroup.getName()));
				} else {
					fLocation.setText(fPreviousExternalLocation);
				}
			}
			fireEvent();
		}
	}
	
	/**
	 * Show a warning when the project location contains files.
	 */
	protected final class DetectGroup extends Observable implements Observer {

		private final Link fWarningText;
		private boolean fDetect;
		
		public DetectGroup(Composite composite) {
			
			fWarningText = new Link(composite, SWT.WRAP);
			GridData gridData= new GridData(GridData.FILL, SWT.FILL, true, true);
			gridData.widthHint= convertWidthInCharsToPixels(50);
			fWarningText.setLayoutData(gridData);
		}
		
	
		public void update(Observable o, Object arg) {
			if (o instanceof LocationGroup) {
				boolean oldDetectState= fDetect;
				if (fLocationGroup.isInWorkspace()) {
					String name= getProjectName();
					if (name.length() == 0 || ResourcesPlugin.getWorkspace().getRoot().findMember(name) != null) {
						fDetect= false;
					} else {
						final File directory= fLocationGroup.getLocation().append(getProjectName()).toFile();
						fDetect= directory.isDirectory();
					}
				} else {
					final File directory= fLocationGroup.getLocation().toFile();
					fDetect= directory.isDirectory();
				}
				
				if (oldDetectState != fDetect) {
					setChanged();
					notifyObservers();
					
					fWarningText.setVisible(true);
					fWarningText.setText(DeeNewWizardMessages.LangNewProject_Page1_DetectGroup_message);
					setMessage("Detect group!", INFORMATION);
				}
			}
		}

		public boolean mustDetect() {
			return fDetect;
		}
		
		public void handlePossibleJVMChange() {
		}

	}
	
	/**
	 * Validate this page and show appropriate warnings and error NewWizardMessages.
	 */
	protected final class Validator implements Observer {

		public void update(Observable o, Object arg) {

			final IWorkspace workspace= ResourcesPlugin.getWorkspace();

			final String name= fNameGroup.getName();

			// check whether the project name field is empty
			if (name.length() == 0) { 
				setErrorMessage(null);
				setMessage(DeeNewWizardMessages.LangNewProject_Page1_Message_enterProjectName); 
				setPageComplete(false);
				return;
			}

			// check whether the project name is valid
			final IStatus nameStatus= workspace.validateName(name, IResource.PROJECT);
			if (!nameStatus.isOK()) {
				setErrorMessage(nameStatus.getMessage());
				setPageComplete(false);
				return;
			}

			// check whether project already exists
			final IProject handle= getProjectHandle();
			if (handle.exists()) {
				setErrorMessage(DeeNewWizardMessages.LangNewProject_Page1_Message_projectAlreadyExists); 
				setPageComplete(false);
				return;
			}

			final String location= fLocationGroup.getLocation().toOSString();

			// check whether location is empty
			if (location.length() == 0) {
				setErrorMessage(null);
				setMessage(DeeNewWizardMessages.LangNewProject_Page1_Message_enterLocation); 
				setPageComplete(false);
				return;
			}

			// check whether the location is a syntactically correct path
			if (!Path.EMPTY.isValidPath(location)) { 
				setErrorMessage(DeeNewWizardMessages.LangNewProject_Page1_Message_invalidDirectory); 
				setPageComplete(false);
				return;
			}

			// check whether the location has the workspace as prefix
			IPath projectPath= Path.fromOSString(location);
			if (!fLocationGroup.isInWorkspace() && Platform.getLocation().isPrefixOf(projectPath)) {
				setErrorMessage(DeeNewWizardMessages.LangNewProject_Page1_Message_cannotCreateInWorkspace); 
				setPageComplete(false);
				return;
			}

			// If we do not place the contents in the workspace validate the
			// location.
			if (!fLocationGroup.isInWorkspace()) {
				final IStatus locationStatus= workspace.validateProjectLocation(handle, projectPath);
				if (!locationStatus.isOK()) {
					setErrorMessage(locationStatus.getMessage());
					setPageComplete(false);
					return;
				}
			}
			
			setPageComplete(true);

			setErrorMessage(null);
			setMessage(null);
		}

	}


	/* ----------------   -----   ---------------- */

	protected NameGroup fNameGroup;
	protected LocationGroup fLocationGroup;
	protected DetectGroup fDetectGroup;
	protected Validator fValidator;


	public LangProjectWizardFirstPage(String pageName) {
		super(pageName);
		setPageComplete(false);
	}
	
	public void createControl(Composite parent) {
		//initializeDialogUnits(parent); 		// dunno about

		// Content composite
		final Composite content= new Composite(parent, SWT.NULL);
		content.setFont(parent.getFont());
		content.setLayout(initGridLayout(new GridLayout(1, false), true));
		content.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		setControl(content);
		
		// create UI elements
		fNameGroup= new NameGroup(content, "");
		fLocationGroup= new LocationGroup(content);
		fNameGroup.addObserver(fLocationGroup);

		// create custom controls
		createCustomControls(content);

		fDetectGroup= new DetectGroup(content);
		fLocationGroup.addObserver(fDetectGroup);

		// initialize all elements
		fNameGroup.notifyObservers();

		// create and connect validator
		fValidator= new Validator();
		fNameGroup.addObserver(fValidator);
		fLocationGroup.addObserver(fValidator);
		
		Dialog.applyDialogFont(content);

		//PlatformUI.getWorkbench().getHelpSystem().setHelp(content, IJavaHelpContextIds.NEW_JAVAPROJECT_WIZARD_PAGE);
	}
	
	protected abstract Observer[] createCustomControls(Composite content);

	public String getProjectName() {
		return fNameGroup.getName();
	}
	
	/**
	 * Creates a project resource handle for the current project name field
	 * value.
	 */
	public IProject getProjectHandle() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(fNameGroup.getName());
	}

	
	@Override
	public IWizardPage getNextPage() {
		return super.getNextPage();
	}
	
	/** Initialize a grid layout with the default Dialog settings. 
	 * XXX: bruno_m: Commented because I don't know what this does. */
	protected GridLayout initGridLayout(GridLayout layout, boolean margins) {
		/*layout.horizontalSpacing= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		if (margins) {
			layout.marginWidth= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		} else {
			layout.marginWidth= 0;
			layout.marginHeight= 0;
		}*/
		return layout;
	}


}