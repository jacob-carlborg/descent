package mmrnmhrm.core.build;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import melnorme.miscutil.Assert;
import melnorme.miscutil.StringUtil;
import melnorme.miscutil.log.Logg;
import mmrnmhrm.core.CoreUtils;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeNameRules;
import mmrnmhrm.core.model.DeeProjectOptions;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;


public class DeeBuilder extends IncrementalProjectBuilder {
	
	protected static IDeeBuilderListener buildListener = 
		new IDeeBuilderListener.NullDeeBuilderListener();
	
	public static void setBuilderListener(IDeeBuilderListener listener) {
		buildListener = listener;
	}

	protected List<IFile> dmodules;
	private IFolder outputFolder;
	
	protected void startupOnInitialize() {
		Assert.isTrue(getModelProject() != null);
	}

	private IScriptProject getModelProject() {
		IScriptProject scriptProject = DLTKCore.create(getProject());
		Assert.isTrue(scriptProject.exists());
		return scriptProject;
	}

	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		
		monitor.beginTask("Building D project", 5);

		IProject project = getProject();
		Logg.builder.println("Doing build ", kind, " for project:", project);

		
		IScriptProject deeProj = getModelProject();
		
		outputFolder = prepOutputFolder(getProjectOptions());

		dmodules = new ArrayList<IFile>();
		
		IBuildpathEntry[] buildpathEntries = deeProj.getResolvedBuildpath(false);

		for (int i = 0; i < buildpathEntries.length; i++) {
			IBuildpathEntry entry = buildpathEntries[i];
			Logg.builder.println("  entry: " + entry);
			if(entry.getEntryKind() == IBuildpathEntry.BPE_SOURCE) {

				IPath entrypath = entry.getPath().removeFirstSegments(1);
				IContainer entryResource = (IContainer) project.findMember(entrypath);
	
				if(!entryResource.getFullPath().equals(outputFolder.getFullPath())) {
					// fixme overwriting copy
					CoreUtils.overwriteCopy(entryResource, outputFolder,
							new SubProgressMonitor(monitor, 1));
				}

				collectModules(outputFolder, monitor);
			} else if(entry.getEntryKind() == IBuildpathEntry.BPE_LIBRARY) {
				if(!entry.isExternal()) {
					IPath entrypath = entry.getPath();
					IProjectFragment projFrag = deeProj.findProjectFragment(entrypath);
					if(projFrag != null)
						Logg.main.println(StringUtil.collToString(projFrag.getChildren(), ","));
					else
						Logg.main.println("No proj frag for: " + entrypath);
				}
				// TODO compiler with the library
			}
		}
		
		monitor.worked(1);
		
		cleanStaleResources(monitor);
		monitor.worked(1);
		
		BudDeeModuleCompiler compiler = new BudDeeModuleCompiler();
		compiler.compileModules(dmodules, deeProj,	monitor);
		monitor.worked(1);
		
		outputFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
		return null; // No deps
	}

	private DeeProjectOptions getProjectOptions() {
		return DeeModel.getDeeProjectInfo(getModelProject());
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

	private void cleanStaleResources(IProgressMonitor monitor) throws CoreException {
		for (IFile dmodule : dmodules) {
			removeDerivateWithExtension(dmodule, "o");
			removeDerivateWithExtension(dmodule, "obj");
		}
	}

	private IPath removeDerivateWithExtension(IFile dmodule, String ext)
			throws CoreException {
		IPath projPath = dmodule.getProjectRelativePath().removeFileExtension();
		IPath path = projPath.addFileExtension(ext);
		IResource resource = getProject().findMember(path);
		if(resource != null)
			resource.delete(true, null);
		return projPath;
	}

	private void collectModules(IFolder findMember, IProgressMonitor monitor) throws CoreException {
		IResource[] members = findMember.members(false);
		for (int i = 0; i < members.length; i++) {
			IResource resource = members[i];
			if(resource.getType() == IResource.FILE) {
				String modName = resource.getName();
				if(DeeNameRules.isValidCompilationUnitName(modName)) {
					dmodules.add((IFile) resource);
					//compileModule((IFile) resource, monitor);
				}
			} else if(resource.getType() == IResource.FOLDER) {
				collectModules((IFolder) resource, monitor);
			}
		}
		
	}
	
/*
	protected void compileModule(IFile file, IProgressMonitor monitor) throws CoreException {
		
		String resourceLoc = file.getLocation().toOSString();
		String[] cmdarray = new String[] {"dmd" , "-c", resourceLoc };
		//String[] envp = null;
		
		final ProcessBuilder builder = new ProcessBuilder(cmdarray);
		builder.directory(file.getLocation().removeLastSegments(1).toFile());

		try {
			final Process proc = builder.start();
			ProcessUtil.waitForProcess(monitor, proc);
		} catch (IOException e) {
			throw DeeCore.createCoreException("D Build: Error exec'ing.", e);
		} catch (InterruptedException e) {
			throw DeeCore.createCoreException("D Build: Interrupted.", e);
		}
	}
*/

	protected void clean(IProgressMonitor monitor) throws CoreException {
		IPath outputPath = getProjectOptions().compilerOptions.outputDir;
		outputFolder = getProject().getFolder(outputPath);
		if(outputFolder.exists())
			outputFolder.delete(true, null);
	}

}
