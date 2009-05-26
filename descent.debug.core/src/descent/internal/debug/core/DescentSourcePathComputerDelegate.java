package descent.internal.debug.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.DirectorySourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.ProjectSourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.WorkspaceSourceContainer;

import descent.core.IJavaProject;
import descent.core.IPackageFragmentRoot;
import descent.core.JavaCore;
import descent.debug.core.IDescentLaunchConfigurationConstants;

public class DescentSourcePathComputerDelegate implements ISourcePathComputerDelegate {

	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException {
		List<ISourceContainer> containers = new ArrayList<ISourceContainer>();
		
		String projectName = configuration.getAttribute(IDescentLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);
		if (projectName != null) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			containers.add(new ProjectSourceContainer(project, false));
			
			IJavaProject javaProject = JavaCore.create(project);
			if (javaProject != null) {
				IPackageFragmentRoot[] roots = javaProject.getAllPackageFragmentRoots();
				for(IPackageFragmentRoot root : roots) {
					if (root.isArchive() || root.isExternal()) {
						containers.add(new DirectorySourceContainer(root.getPath(), false));
					}
				}
			}
		} else {
			containers.add(new WorkspaceSourceContainer());
		}
		
		return containers.toArray(new ISourceContainer[containers.size()]);
	}

}
