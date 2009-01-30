/**
 * 
 */
package descent.ui.metrics;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;


class ProjectBuilder implements IRunnableWithProgress {
    public void run(IProgressMonitor monitor) {
        IProject[] projects = getProjectsToRebuild();
        for (int i = 0; i < projects.length; i++) {
            try {
                projects[i].build(IncrementalProjectBuilder.FULL_BUILD, MetricsBuilder.BUILDER_ID, null, monitor);
            } catch (CoreException cex) {
                MetricsPlugin.getDefault().getLog().log(cex.getStatus());
            }
        }
    }
    
    private IProject[] getProjectsToRebuild() {
        IProject allProjects[] = MetricsPlugin.getWorkspace().getRoot().getProjects();

        ArrayList projectsToRebuild = new ArrayList();
        for (int i = 0; i < allProjects.length; i++) {
            if (shouldRebuildProject(allProjects[i])) {
                projectsToRebuild.add(allProjects[i]);
            }
        }

        return (IProject[]) projectsToRebuild.toArray(new IProject[projectsToRebuild.size()]);
    }

    private boolean shouldRebuildProject(IProject project) {
        try {
            return project.hasNature(MetricsNature.NATURE_ID);
        } catch (CoreException cex) {
            MetricsPlugin.getDefault().getLog().log(cex.getStatus());
            return false;
        }
    }
}