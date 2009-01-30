package descent.ui.metrics.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import descent.core.IJavaProject;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

import descent.ui.metrics.MetricsBuilder;
import descent.ui.metrics.MetricsNature;
import descent.ui.metrics.MetricsPlugin;

public final class MetricsPropertiesPage extends PropertyPage {
    private ExcludedResourcesView excludedResources;
    private Button enabledButton;
    private boolean rebuildRequired;

    public boolean performOk() {
        try {
            performOkProtected();
        } catch (Exception ex) {
            showException("Caught exception while performing OK", ex);
            return false;
        }

        return true;
    }

    private void performOkProtected() throws CoreException, InvocationTargetException, InterruptedException {
        rebuildRequired |= excludedResources.isModified();
        excludedResources.store();
        performActivateOrDeactivate();
        buildProject();
    }

    private void showException(String message, Exception ex) {
        IStatus status;
        if (ex instanceof CoreException) {
            status = ((CoreException) ex).getStatus();
        } else {
            status = new Status(IStatus.ERROR, MetricsPlugin.PLUGIN_ID, IStatus.OK, message, ex);
        }

        MetricsPlugin.getDefault().getLog().log(status);
        ErrorDialog.openError(getShell(), "CoreException", ex.getMessage(), status);
    }

    protected Control createContents(Composite parent) {
        noDefaultAndApplyButton();
        Composite composite = new Composite(parent, SWT.NULL);
        try {
            initialiseLayout(composite);
            initialiseEnableCheckBox(composite);
            excludedResources = new ExcludedResourcesView(composite, getProject());
        } catch (Exception ex) {
            showException("Caught exception", ex);
        }

        return composite;
    }

    private void performActivateOrDeactivate() throws CoreException, InvocationTargetException, InterruptedException {
        boolean pluginActive = isPluginActive();

        if (enabledButton.getSelection() && !pluginActive) {
            activatePlugin();
        } else if (!enabledButton.getSelection() && pluginActive) {
            deactivatePlugin();
        }
    }

    private boolean isPluginActive() throws CoreException {
        return getProject().hasNature(MetricsNature.NATURE_ID);
    }

    private void activatePlugin() throws InvocationTargetException, InterruptedException {
        new ProgressMonitorDialog(getShell()).run(true, true, new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) {
                try {
                    addNatureToProject(monitor);
                } catch (CoreException cex) {
                    MetricsPlugin.getDefault().getLog().log(cex.getStatus());
                }
            }
        });

        rebuildRequired = true;
    }

    private void deactivatePlugin() throws CoreException, InvocationTargetException, InterruptedException {
        new ProgressMonitorDialog(getShell()).run(true, true, new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) {
                try {
                    removeNatureFromProject(monitor);
                } catch (CoreException cex) {
                    MetricsPlugin.getDefault().getLog().log(cex.getStatus());
                }
            }
        });

        removeMarkersForAllResources();
    }

    private void buildProject() throws InvocationTargetException, InterruptedException {
        if (!rebuildRequired) {
            return;
        }

        rebuild();
        rebuildRequired = false;
    }

    private void rebuild() throws InvocationTargetException, InterruptedException {
        new ProgressMonitorDialog(getShell()).run(true, true, new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) {
                try {
                    getProject().build(IncrementalProjectBuilder.FULL_BUILD, monitor);
                } catch (CoreException cex) {
                    MetricsPlugin.getDefault().getLog().log(cex.getStatus());
                }
            }
        });
    }

    private void addNatureToProject(IProgressMonitor monitor) throws CoreException {
        IProjectDescription description = getProject().getDescription();
        ArrayList natures = new ArrayList(Arrays.asList(description.getNatureIds()));
        natures.add(MetricsNature.NATURE_ID);
        description.setNatureIds((String[]) natures.toArray(new String[natures.size()]));
        getProject().setDescription(description, monitor);
    }

    private void removeNatureFromProject(IProgressMonitor monitor) throws CoreException {
        IProjectDescription description = getProject().getDescription();
        ArrayList natures = new ArrayList(Arrays.asList(description.getNatureIds()));
        natures.remove(MetricsNature.NATURE_ID);
        description.setNatureIds((String[]) natures.toArray(new String[natures.size()]));
        getProject().setDescription(description, monitor);
    }

    private IProject getProject() {
        return ((IJavaProject) getElement()).getProject();
    }

    private void initialiseEnableCheckBox(Composite parent) throws CoreException {
        enabledButton = new Button(parent, SWT.CHECK | SWT.LEFT);
        enabledButton.setText("Enable Metrics Gathering");

        enabledButton.setSelection(isPluginActive());
    }

    private void initialiseLayout(Composite parent) {
        GridLayout layout = new GridLayout(1, false);
        parent.setLayout(layout);
        parent.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    private void removeMarkersForAllResources() throws CoreException {
        getProject().deleteMarkers(MetricsBuilder.MARKER_ID, true, IResource.DEPTH_INFINITE);
    }
}