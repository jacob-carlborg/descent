package mmrnmhrm.core.build;

import static melnorme.miscutil.Assert.assertTrue;

import java.util.Map;

import melnorme.miscutil.Assert;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeProjectOptions;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;

import dtool.Logg;


public class DeeProjectBuilder extends IncrementalProjectBuilder {
	
	protected static IDeeBuilderListener buildListener = 
		new IDeeBuilderListener.NullDeeBuilderListener();
	
	public static void setBuilderListener(IDeeBuilderListener listener) {
		buildListener = listener;
	}

	
	private IFolder outputFolder;
	
	protected void startupOnInitialize() {
		assertTrue(getModelProject() != null);
	}

	private IScriptProject getModelProject() {
		IScriptProject scriptProject = DLTKCore.create(getProject());
		Assert.isTrue(scriptProject.exists());
		return scriptProject;
	}
	
	private DeeProjectOptions getProjectOptions() {
		return DeeModel.getDeeProjectInfo(getModelProject());
	}
	

	protected void clean(IProgressMonitor monitor) throws CoreException {
		IPath outputPath = getProjectOptions().compilerOptions.outputDir;
		outputFolder = getProject().getFolder(outputPath);
		if(outputFolder.exists()) {
			outputFolder.delete(true, null);
			outputFolder.create(true, true, monitor);
		}
	}

	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		
		IProject project = getProject();
		Logg.builder.println("Doing build ", kind, " for project:", project);

		IScriptProject deeProj = getModelProject();
		DeeBuilder compiler = new DeeBuilder();

		monitor.beginTask("Building D project", 5);
		
		outputFolder = prepOutputFolder(getProjectOptions());
		monitor.worked(1);
		
		compiler.collectBuildUnits(deeProj, monitor);
		monitor.worked(1);
		
		compiler.compileModules(deeProj, monitor);
		monitor.worked(1);
		
		outputFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
		return null; // No deps
	}



	private IFolder prepOutputFolder(DeeProjectOptions options) throws CoreException {
		IPath outputPath = options.compilerOptions.outputDir;
		outputFolder = getProject().getFolder(outputPath);
		if(outputFolder.exists()) {
			outputFolder.delete(true, null);
		}

		if(!outputFolder.exists())
			outputFolder.create(IResource.DERIVED, true, null);
		return outputFolder;
	}
	
}
