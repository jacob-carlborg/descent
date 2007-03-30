package descent.ui.metrics.export;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.QualifiedName;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.JavaCore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import descent.ui.metrics.MetricsPlugin;

public final class MetricsExportWizardPage extends WizardPage implements IPropertyChangeListener, SelectionListener {
    private StringFieldEditor projectText;
    private StringFieldEditor exportDirectoryText;
    private IntegerFieldEditor htmlRowsPerPageEditor;
    private Button htmlExportCheckbox;
    private Button htmlImagesCheckbox;
    private Button csvExportCheckbox;
    private ISelection selection;

    public MetricsExportWizardPage(ISelection selection) {
        super("Metrics Export");
        setTitle("Metrics Export");
        setDescription("This wizard exports the metrics of a Java project");
        this.selection = selection;
    }

    public void createControl(Composite parent) {
        Composite rootComposite = createControlsContainer(parent);

        try {
            initialize();
        } catch (RuntimeException rex) {
            throw rex;
        } catch (CoreException cex) {
            MetricsPlugin.log(cex);
            throw new RuntimeException("Caught CoreException. See log for details.");
        }
        dialogChanged();
        setControl(rootComposite);
    }

    private Composite createControlsContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.verticalSpacing = 20;
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        createCommonControls(container);
        createExportTypesControls(container);
        return container;
    }

    private void createCommonControls(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.verticalSpacing = 9;
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        createProjectControls(container);
        createExportDirectoryControls(container);
    }

    private void createExportDirectoryControls(Composite container) {
        exportDirectoryText = addStringFieldEditor(container, "Export Directory");

        Button button = new Button(container, SWT.PUSH);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                handleBrowseHtmlExportLocation();
            }
        });
    }

    private void createProjectControls(Composite container) {
        projectText = addStringFieldEditor(container, "Project");

        Button button = new Button(container, SWT.PUSH);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                try {
                    handleBrowseProjects();
                } catch (CoreException cex) {
                    MetricsPlugin.log(cex);
                    throw new RuntimeException("Caught CoreException. See log for details.");
                }
            }
        });
    }

    private StringFieldEditor addStringFieldEditor(Composite container, String labelText) {
        Label label = new Label(container, SWT.NULL);
        label.setText(labelText);

        Composite editorComposite = new Composite(container, SWT.NULL);
        editorComposite.setLayout(new GridLayout());
        editorComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        StringFieldEditor editor = new StringFieldEditor("", "", editorComposite);

        editor.setPropertyChangeListener(this);

        return editor;
    }

    private void createExportTypesControls(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.verticalSpacing = 9;
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        createHtmlExportControls(container);
        createCsvExportControls(container);
    }

    private void createHtmlExportControls(Composite container) {
        createHtmlExportCheckBox(container);
        createHtmlRowsPerPageControl(container);
        createHtmlImagesCheckBox(container);
    }

    private void createHtmlExportCheckBox(Composite container) {
        Label label = new Label(container, SWT.NULL);
        label.setText("Export HTML");

        htmlExportCheckbox = new Button(container, SWT.CHECK);

        htmlExportCheckbox.addSelectionListener(this);
        new Label(container, SWT.NULL);
    }

    private void createHtmlRowsPerPageControl(Composite container) {
        new Label(container, SWT.NULL);

        Label label = new Label(container, SWT.NULL);
        label.setText("Rows per Page");

        Composite editorComposite = new Composite(container, SWT.NULL);
        editorComposite.setLayout(new GridLayout());
        editorComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        htmlRowsPerPageEditor = new IntegerFieldEditor("", "", editorComposite);

        htmlRowsPerPageEditor.setPropertyChangeListener(this);
    }

    private void createHtmlImagesCheckBox(Composite container) {
        new Label(container, SWT.NULL);
        Label label = new Label(container, SWT.NULL);
        label.setText("Histograms and Dashboard (Not Mac OS X)");

        htmlImagesCheckbox = new Button(container, SWT.CHECK);
    }

    private void createCsvExportControls(Composite container) {
        Label label = new Label(container, SWT.NULL);
        label.setText("Export CSV");

        csvExportCheckbox = new Button(container, SWT.CHECK);

        csvExportCheckbox.addSelectionListener(this);
        new Label(container, SWT.NULL);
    }

    private void initialize() throws CoreException {
        if (selection == null || selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
            return;
        }

        IStructuredSelection ssel = (IStructuredSelection) selection;
        if (ssel.size() == 1) {
            initialiseFromSelectedObject(ssel.getFirstElement());
        }
    }

    private void initialiseFromSelectedObject(Object obj) throws CoreException {
        if (obj instanceof IJavaElement) {
            initialiseFromJavaProject(((IJavaElement) obj).getJavaProject());
        } else if (obj instanceof IResource) {
            initialiseFromProject(((IResource) obj).getProject());
        }
    }

    private void initialiseFromProject(IProject project) throws CoreException {
        IJavaProject javaProject = JavaCore.create(project);
        if (javaProject != null && javaProject.exists()) {
            initialiseFromJavaProject(javaProject);
        }
    }

    private void initialiseFromJavaProject(IJavaProject javaProject) throws CoreException {
        projectText.setStringValue(javaProject.getPath().toString());
        IProject project = javaProject.getProject();
        initialiseExportDirectoryText(project);
        initialiseHtmlExportEnabledEditor(project);
        initialiseHtmlRowsPerPageEditor(project);
        initialiseHtmlImagesEnabledEditor(project);
        initialiseCsvExportEnabledEditor(project);
    }

    private void initialiseHtmlRowsPerPageEditor(IProject project) throws CoreException {
        String rowsPerPage = project.getPersistentProperty(MetricsExportWizard.HTML_ROWS_PER_PAGE_QUALIFIED_NAME);
        if (rowsPerPage != null) {
            htmlRowsPerPageEditor.setStringValue(rowsPerPage);
        } else {
            htmlRowsPerPageEditor.setStringValue("");
        }
    }

    private void initialiseHtmlExportEnabledEditor(IProject project) throws CoreException {
        initialiseBooleanEditor(project, htmlExportCheckbox, MetricsExportWizard.HTML_EXPORT_ENABLED_QUALIFIED_NAME, true);
    }

    private void initialiseHtmlImagesEnabledEditor(IProject project) throws CoreException {
        initialiseBooleanEditor(project, htmlImagesCheckbox, MetricsExportWizard.HTML_IMAGE_PRODUCTION_ENABLED_QUALIFIED_NAME, true);
    }

    private void initialiseExportDirectoryText(IProject project) throws CoreException {
        String exportDir = project.getProject().getPersistentProperty(MetricsExportWizard.DIRECTORY_QUALIFIED_NAME);
        if (exportDir != null) {
            exportDirectoryText.setStringValue(exportDir);
        } else {
            exportDirectoryText.setStringValue("");
        }
    }

    private void initialiseCsvExportEnabledEditor(IProject project) throws CoreException {
        initialiseBooleanEditor(project, csvExportCheckbox, MetricsExportWizard.CSV_EXPORT_ENABLED_QUALIFIED_NAME, false);
    }

    private void initialiseBooleanEditor(IProject project, Button editor, QualifiedName propertyName, boolean defaultValue) throws CoreException {
        String enabled = project.getPersistentProperty(propertyName);
        boolean value;
        if (enabled != null) {
            value = Boolean.valueOf(enabled).booleanValue();
        } else {
            value = defaultValue;
        }

        editor.setSelection(value);
    }

    private void handleBrowseHtmlExportLocation() {
        DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.SINGLE | SWT.OPEN);
        String path = dialog.open();
        if (path != null) {
            exportDirectoryText.setStringValue(path);
        }
    }

    private void handleBrowseProjects() throws CoreException {
        ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(), false, "Select project");
        if (dialog.open() == Window.OK) {
            Object[] result = dialog.getResult();
            if (result.length == 1) {
                initialiseFromSelectedObject(MetricsPlugin.getWorkspace().getRoot().getFile((IPath) result[0]));
            }
        }
    }

    private void dialogChanged() {
        if (!isProjectSpecified()) {
            updateStatus("Project must be specified");
        } else if (!isExportDirectorySpecified()) {
            updateStatus("Directory must be specified");
        } else if (!isRowsPerPageValid()) {
            updateStatus("Rows per page must be a positive integer or blank (no paging)");
        } else if (!isAtLeastOneExportFormatSpecified()) {
            updateStatus("At least one export format must be specified");
        } else {
            updateStatus(null);
        }
    }

    private boolean isAtLeastOneExportFormatSpecified() {
        return isCsvExportEnabled() || isHtmlExportEnabled();
    }

    private boolean isRowsPerPageValid() {
        return !isHtmlExportEnabled() || htmlRowsPerPageEditor.getStringValue().trim().length() == 0 || htmlRowsPerPageEditor.isValid() && htmlRowsPerPageEditor.getIntValue() > 0;
    }

    private boolean isExportDirectorySpecified() {
        return getExportDirectoryPath().length() != 0;
    }

    private boolean isProjectSpecified() {
        return getProject().length() != 0;
    }

    private void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }

    public String getProject() {
        return projectText.getStringValue();
    }

    public String getExportDirectoryPath() {
        return exportDirectoryText.getStringValue();
    }

    public int getHtmlRowsPerPage() {
        if (htmlRowsPerPageEditor.getStringValue().trim().length() == 0) {
            return Integer.MAX_VALUE;
        } else {
            return htmlRowsPerPageEditor.getIntValue();
        }
    }

    public boolean isHtmlExportEnabled() {
        return htmlExportCheckbox.getSelection();
    }

    public boolean isHtmlImageProductionEnabled() {
        return htmlImagesCheckbox.getSelection();
    }

    public boolean isCsvExportEnabled() {
        return csvExportCheckbox.getSelection();
    }

    public void propertyChange(PropertyChangeEvent event) {
        dialogChanged();
    }

    public void widgetSelected(SelectionEvent e) {
        dialogChanged();
    }

    public void widgetDefaultSelected(SelectionEvent e) {
        dialogChanged();
    }
}
