package mmrnmhrm.core.build;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeNature;
import mmrnmhrm.core.model.DeeProject0;
import mmrnmhrm.core.model.DeeSourceFolder;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import util.Assert;
import util.Logg;

public class DeeBuilder extends IncrementalProjectBuilder {

	class DeeModuleCollector implements IResourceVisitor {

		List<IResource> dmodules = new ArrayList<IResource>();
		
		public boolean visit(IResource resource) throws CoreException {
			if(resource.getFullPath().lastSegment().endsWith(".d")) {
				dmodules.add(resource);
				Logg.println(resource.getFullPath());
			}
			return true;
		}
	}
	
	private DeeProject0 deeProject;
	
	public DeeProject0 getDeeProject() {
		deeProject = DeeModel.getInstance().getDeeProject(getProject());
		return deeProject;
	}

	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {

		
		IMarker marker = getProject().createMarker(IMarker.PROBLEM);
		Assert.isTrue(marker.exists());
		marker.setAttribute(IMarker.MESSAGE, "Test Marker Message");
		marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);

		getProject().getNature(DeeNature.NATURE_FQID);
		
		DeeModuleCollector visitor = new DeeModuleCollector();

		for(DeeSourceFolder dsf : getDeeProject().sourceFolders) {
			dsf.folder.accept(visitor, IResource.DEPTH_INFINITE, IResource.NONE);
		}
		
		DMDCompilerEnviron dce = new DMDCompilerEnviron(getDeeProject());
		dce.compileModules(visitor.dmodules);
		
		
		return null; // No deps
	}

	protected void startupOnInitialize() {
		// add builder init logic here
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
		getProject().deleteMarkers(IMarker.PROBLEM, true,
				IResource.DEPTH_INFINITE);
	}

	
}
