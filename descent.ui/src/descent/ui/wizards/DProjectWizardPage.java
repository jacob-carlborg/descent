package descent.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

class DProjectWizardPage extends WizardPage {
	
	private final static String PAGE_NAME = "DProjectWizardPage";
	
	private Text uiName;
	private Button uiUseDefaultLocation;
	private Label uiLocationLabel;
	private Text uiLocation;
	private Button uiBrowseLocation;

	protected DProjectWizardPage() {
		super(PAGE_NAME);
		setPageComplete(false);
		setTitle("New D Project");
		setDescription("Create a new D project");
	}
	
	public String getName() {
		return uiName.getText();
	}
	
	public boolean isUseDefaultLocation() {
		return uiUseDefaultLocation.getSelection();
	}
	
	public IPath getLocation() {
		return Path.fromOSString(uiLocation.getText());
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		
		GridLayout layout;
		GridData data;
		Label label;
		
		layout = new GridLayout(2, false);
		composite.setLayout(layout);
		
		label = new Label(composite, SWT.NONE);
		label.setText("Name:");
		
		uiName = new Text(composite, SWT.SINGLE | SWT.BORDER);
		uiName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		uiName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (uiUseDefaultLocation.getSelection()) {
					updateDefaultLocation();
				}
				validate();
			}
		}); 
		
		Group location = new Group(composite, SWT.NONE);
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		location.setLayoutData(data);
		
		layout = new GridLayout(3, false);
		location.setLayout(layout);
		
		uiUseDefaultLocation = new Button(location, SWT.CHECK);
		uiUseDefaultLocation.setText("Use default location");
		uiUseDefaultLocation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selection = uiUseDefaultLocation.getSelection();
				
				uiLocation.setEnabled(!selection);
				uiLocationLabel.setEnabled(!selection);
				uiBrowseLocation.setEnabled(!selection);
				
				if (selection) {
					updateDefaultLocation();
				} else {
					uiLocation.setText("");
				}
				
				validate();
			}
		});
		
		data = new GridData();
		data.horizontalSpan = 3;
		uiUseDefaultLocation.setLayoutData(data);
		
		uiLocationLabel = new Label(location, SWT.NONE);
		uiLocationLabel.setText("Location:");
		
		uiLocation = new Text(location, SWT.SINGLE | SWT.BORDER);
		uiLocation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		uiBrowseLocation = new Button(location, SWT.PUSH);
		uiBrowseLocation.setText("Browse...");
		uiBrowseLocation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setMessage("Select a directory");
				String path = dialog.open();
				if (path != null) {
					uiLocation.setText(path);
				}
			}
		});
		
		setControl(composite);
		
		setInitialValues();
		
		uiUseDefaultLocation.setSelection(true);
		
		// Add the listener after the initial value, so that no 
		// error message appear
		uiLocation.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});
	}
	
	private void updateDefaultLocation() {
		uiLocation.setText(Platform.getLocation().append(uiName.getText()).toOSString());
	}

	private void setInitialValues() {
		uiLocationLabel.setEnabled(false);
		uiLocation.setEnabled(false);
		uiBrowseLocation.setEnabled(false);
		
		uiLocation.setText(Platform.getLocation().toOSString());
	}
	
	private boolean isInWorkspace() {
		return uiUseDefaultLocation.getSelection();
	}
	
	public IProject getProjectHandle() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(uiName.getText());
	}
	
	private void validate() {
		String name = uiName.getText();
		
		if (name.trim().length() == 0) {
			setErrorMessage("The name cannot be empty");
			setPageComplete(false);
			return;
		}
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IStatus nameStatus = workspace.validateName(name, IResource.PROJECT);
		if (!nameStatus.isOK()) {
			setErrorMessage("The location is not valid");
			setPageComplete(false);
			return;
		}
		
		IProject handle = getProjectHandle();
		
		String location = uiLocation.getText();
		
		if (uiLocation.getText().trim().length() == 0) {
			setErrorMessage("The location cannot be empty");
			setPageComplete(false);
		}
		
		// check whether the location is a syntactically correct path
		if (!Path.EMPTY.isValidPath(location)) { 
			setErrorMessage("The location is not valid"); 
			setPageComplete(false);
			return;
		}

		// check whether the location has the workspace as prefix
		IPath projectPath= Path.fromOSString(location);
		if (!isInWorkspace() && Platform.getLocation().isPrefixOf(projectPath)) {
			setErrorMessage("Cannot create a project in the workspace"); 
			setPageComplete(false);
			return;
		}

		// If we do not place the contents in the workspace validate the
		// location.
		if (!isInWorkspace()) {
			final IStatus locationStatus= workspace.validateProjectLocation(handle, projectPath);
			if (!locationStatus.isOK()) {
				setErrorMessage("The location is not valid");
				setPageComplete(false);
				return;
			}
		}
		
		setPageComplete(true);
		setErrorMessage(null);
	}

}
