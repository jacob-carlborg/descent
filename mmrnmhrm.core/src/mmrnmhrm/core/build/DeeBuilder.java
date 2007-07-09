package mmrnmhrm.core.build;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import melnorme.miscutil.StringUtil;
import melnorme.miscutil.log.Logg;
import mmrnmhrm.core.model.DeeModelManager;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.DeeSourceFolder;
import mmrnmhrm.core.model.IDeeSourceRoot;
import mmrnmhrm.core.model.lang.LangSourceFolder;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;


public class DeeBuilder extends IncrementalProjectBuilder {

	class DeeModuleCollector implements IResourceVisitor {

		List<IResource> dmodules = new ArrayList<IResource>();
		
		public boolean visit(IResource resource) throws CoreException {
			if(resource.getFullPath().lastSegment().endsWith(".d")) {
				dmodules.add(resource);
				//Logg.builder.println(resource.getFullPath());
			}
			return true;
		}
	}
	
	private DeeProject deeProject;
	
	public DeeProject getDeeProject() {
		deeProject = DeeModelManager.getLangProject(getProject());
		return deeProject;
	}

	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {

		DeeModuleCollector visitor = new DeeModuleCollector();

		for(IDeeSourceRoot bpentry : getDeeProject().getSourceRoots()) {
			if(bpentry instanceof DeeSourceFolder) {
				LangSourceFolder dsf = (LangSourceFolder) bpentry;
				dsf.folder.accept(visitor, IResource.DEPTH_INFINITE, IResource.NONE);
			}
		}
		
		Logg.builder.println("Got Sources:");
		Logg.builder.println(" ", StringUtil.collToString(visitor.dmodules, ","));
		if(true)
			return null; // Don't do anything since builder isn't working
		
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
