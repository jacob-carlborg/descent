package descent.ui.metrics.export;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import descent.ui.metrics.MetricsBuilder;
import descent.ui.metrics.MetricsPlugin;
import descent.ui.metrics.export.html.HtmlExporter;

public final class MetricsExportWizard extends Wizard implements INewWizard {
    static final QualifiedName DIRECTORY_QUALIFIED_NAME = new QualifiedName(MetricsPlugin.PLUGIN_ID, "exportDirectory");
    static final QualifiedName HTML_ROWS_PER_PAGE_QUALIFIED_NAME = new QualifiedName(MetricsPlugin.PLUGIN_ID, "htmlExportRowsPerPage");
    static final QualifiedName HTML_EXPORT_ENABLED_QUALIFIED_NAME = new QualifiedName(MetricsPlugin.PLUGIN_ID, "htmlExportEnabled");
    static final QualifiedName HTML_IMAGE_PRODUCTION_ENABLED_QUALIFIED_NAME = new QualifiedName(MetricsPlugin.PLUGIN_ID, "htmlImageProductionEnabled");
    static final QualifiedName CSV_EXPORT_ENABLED_QUALIFIED_NAME = new QualifiedName(MetricsPlugin.PLUGIN_ID, "csvExportEnabled");

    private MetricsExportWizardPage page;
    private ISelection selection;

    public MetricsExportWizard() {
        super();
        setNeedsProgressMonitor(true);
    }

    public void addPages() {
        page = new MetricsExportWizardPage(selection);
        addPage(page);
    }

    public boolean performFinish() {
        persistExportProperties();

        final Exporter[] exporters = createExporterList();
        final String project = page.getProject();

        return runOperationForContainer(createFinishingRunnable(exporters, project));
    }

    private IRunnableWithProgress createFinishingRunnable(final Exporter[] exporters, final String project) {
        return new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                try {
                    doFinish(project, exporters, monitor);
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };
    }

    private Exporter[] createExporterList() {
        File directory = new File(page.getExportDirectoryPath());

        List list = new ArrayList();
        addCsvExporterIfRequired(directory, list);
        addHtmlExporterIfRequired(directory, list);

        return (Exporter[]) list.toArray(new Exporter[list.size()]);
    }

    private void addHtmlExporterIfRequired(File directory, List list) {
        if (page.isHtmlExportEnabled()) {
            String project = page.getProject();
            list.add(new HtmlExporter(directory, project, page.getHtmlRowsPerPage(), page.isHtmlImageProductionEnabled()));
        }
    }

    private void addCsvExporterIfRequired(File directory, List list) {
        if (page.isCsvExportEnabled()) {
            list.add(new CsvExporter(directory));
        }
    }

    private boolean runOperationForContainer(IRunnableWithProgress op) {
        try {
            getContainer().run(true, true, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            MetricsPlugin.log(e.getTargetException());
            MessageDialog.openError(getShell(), "Error", e.getTargetException().getMessage());
            return false;
        }

        return true;
    }

    private void doFinish(String projectName, Exporter[] exporters, IProgressMonitor monitor) throws CoreException {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IResource resource = root.findMember(new Path(projectName));
        IJavaProject javaProject = JavaCore.create(resource).getJavaProject();
        validateProject(projectName, javaProject);

        IProject project = javaProject.getProject();

        startExport(exporters, monitor, project);
    }

    private void startExport(Exporter[] exporters, IProgressMonitor monitor, IProject project) throws CoreException {
        try {
            new MetricsBuilder().export(project, exporters, monitor);
        } catch (IOException ioex) {
            throw new CoreException(new Status(IStatus.ERROR, "Failed to write metrics", IStatus.OK, ioex.getMessage(), ioex));
        }
    }

    private void validateProject(String projectName, IJavaElement project) throws CoreException {
        if (project == null || !project.exists() || !(project instanceof IJavaProject)) {
            throw new CoreException(new Status(IStatus.ERROR, "Java Project \"" + projectName + "\" does not exist.", IStatus.OK, "Java Project \"" + projectName + "\" does not exist.", null));
        }
    }

    private void persistExportProperties() {
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(page.getProject());
        try {
            project.setPersistentProperty(MetricsExportWizard.DIRECTORY_QUALIFIED_NAME, new File(page.getExportDirectoryPath()).getAbsolutePath());
            project.setPersistentProperty(MetricsExportWizard.HTML_ROWS_PER_PAGE_QUALIFIED_NAME, Integer.toString(page.getHtmlRowsPerPage()));
            project.setPersistentProperty(MetricsExportWizard.HTML_EXPORT_ENABLED_QUALIFIED_NAME, Boolean.toString(page.isHtmlExportEnabled()));
            project.setPersistentProperty(MetricsExportWizard.HTML_IMAGE_PRODUCTION_ENABLED_QUALIFIED_NAME, Boolean.toString(page.isHtmlImageProductionEnabled()));
            project.setPersistentProperty(MetricsExportWizard.CSV_EXPORT_ENABLED_QUALIFIED_NAME, Boolean.toString(page.isCsvExportEnabled()));
        } catch (CoreException cex) {
            noteException(cex);
        }
    }

    private void noteException(CoreException cex) {
        MetricsPlugin.log(cex);
        throw new RuntimeException("An error occurred. Please see the log for details.");
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
    }
}
