package mmrnmhrm.core.build;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import melnorme.miscutil.StringUtil;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeNameRules;
import mmrnmhrm.core.model.DeeProjectOptions;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;

import dtool.Logg;

public class DeeBuilder {

	private List<String> buildModules;
	private List<String> buildElements;
	private IPath compilerPath;

	public DeeBuilder() {
		buildElements = new ArrayList<String>();
		buildModules = new ArrayList<String>();
		compilerPath = null;
	}

	public static List<String> getDemoCmdLine(IScriptProject deeProj,
			DeeProjectOptions overlayOptions, IProgressMonitor monitor) {
		DeeBuilder builder = new DeeBuilder();
		try {
			builder.collectBuildUnits(deeProj, true, monitor);
		} catch (CoreException e) {
			DeeCore.log(e);
			return Collections.singletonList(
					"Cannot determine preview: " + e) ;
		}
		builder.buildModules = Collections.singletonList("<<files.d>>");
		//DeeProjectOptions options = DeeModel.getDeeProjectInfo(deeProj);
		return builder.createCommandLine(deeProj, overlayOptions);
	}
	
	public void collectBuildUnits(IScriptProject deeProj,
			IProgressMonitor monitor) throws CoreException  {
		collectBuildUnits(deeProj, false, monitor);
	}
	
	private void collectBuildUnits(IScriptProject deeProj, boolean entriesOnly,
			IProgressMonitor monitor) throws CoreException  {
		
		IProject project = deeProj.getProject();
		
		IBuildpathEntry[] buildpathEntries = deeProj.getResolvedBuildpath(true);

		for (int i = 0; i < buildpathEntries.length; i++) {
			IBuildpathEntry entry = buildpathEntries[i];
			Logg.builder.println("Builder:: In entry: " + entry);

			
			if(entry.getEntryKind() == IBuildpathEntry.BPE_SOURCE) {
				//fixme, what if external
				IPath entrypath;
				IContainer entryResource = null;
				if(!entry.isExternal()) {
					IPath localpath = entry.getPath().removeFirstSegments(1);
					entryResource = (IContainer) project.findMember(localpath);
					entrypath = entryResource.getLocation();
				} else {
					entrypath = entry.getPath();
				}
				buildElements.add("-I"+entrypath.toOSString());
				if(entriesOnly)	
					continue;

				if(entryResource != null)
					proccessContainer(entrypath, entryResource, monitor);
			} else if(entry.getEntryKind() == IBuildpathEntry.BPE_LIBRARY) {
				if(!entry.isExternal()) {
					IPath entryFullPath = entry.getPath();
					IProjectFragment projFrag = deeProj.findProjectFragment(entryFullPath);
					if(projFrag != null)
						Logg.main.println(StringUtil.collToString(projFrag.getChildren(), ","));
					else
						Logg.main.println("No proj fragment for: " + entryFullPath);
				} else if (entry.getPath().lastSegment().matches("phobos")) {
					buildElements.add("-I"+entry.getPath().toOSString());
					// FIXME: BUILDER: Support other kinds of install locations
					IPath path = entry.getPath().removeLastSegments(2);
					compilerPath = path.append("bin");
				}
			}
		}
	}

	protected void proccessContainer(IPath entrypath, IContainer container,
			IProgressMonitor monitor) throws CoreException {
		
		IResource[] members = container.members(false);
		for (int i = 0; i < members.length; i++) {
			IResource resource = members[i];
			processResource(entrypath, resource);
			if(resource.getType() == IResource.FOLDER) {
				proccessContainer(entrypath, (IFolder) resource, monitor);
			} 
		}

	}

	
	protected void processResource(IPath entrypath, IResource resource) {
		if(resource.getType() == IResource.FILE) {
			String modUnitName = resource.getName();
			IPath path = resource.getProjectRelativePath();
			String extName = path.getFileExtension();
			String modName = path.removeFileExtension().lastSegment();
			if(DeeNameRules.isValidCompilationUnitName(modUnitName)) {
				buildModules.add(resource.getLocation().toOSString());
				//addCompileBuildUnit(resource);
			} else {
			}
		}
	}

	protected void compileModules(IScriptProject deeProj,
			IProgressMonitor monitor) throws CoreException {
		
		DeeProjectOptions options = DeeModel.getDeeProjectInfo(deeProj);
		IFolder outputFolder = options.getOutputFolder();
		
		List<String> cmdlineParams = createCommandLine(deeProj, options);
		
		IFile file = outputFolder.getFile(options.getArtifactNameNoExt()+".brf");
		
		byte[] buf = StringUtil.collToString(cmdlineParams, "\n").getBytes();
		InputStream is = new ByteArrayInputStream(buf);
		if(file.exists() == false) {
			file.create(is, false, null);
		} else {
			file.setContents(is, IResource.NONE, null);
		}

		Logg.main.println(">>> " + cmdlineParams);
		DeeProjectBuilder.buildListener.clear();
		DeeProjectBuilder.buildListener.println(">>> " + cmdlineParams);

		String exe = options.compilerOptions.buildTool;
		String rspfile = "@" + file.getProjectRelativePath().toOSString();
		
		IPath workDir = deeProj.getProject().getLocation();
		runBuildProcess(monitor, workDir , exe, rspfile);

	}

	protected List<String> createCommandLine(IScriptProject deeProj,
			DeeProjectOptions options) {
		
		List<String> cmdline = new ArrayList<String>();
		
		cmdline.addAll(buildElements);
		
		if(compilerPath != null)
			cmdline.add("-DCPATH"+compilerPath.toOSString());
		

		IPath outputPath = options.getOutputFolder().getProjectRelativePath();
		cmdline.add("-od"+outputPath);

		cmdline.add("-Rn");
		String appname;
		appname = outputPath.append(options.getArtifactName()).toOSString();
		cmdline.add("-T"+appname);
		if(options.getExtraOptions().length() != 0) {
			String[] extrasOpts = options.getExtraOptions().split("\r\n|\n");
			for (int i = 0; i < extrasOpts.length; i++) {
				cmdline.add(extrasOpts[i]);
			}
		}
		
		cmdline.addAll(buildModules);

		return cmdline;
	}


	private void runBuildProcess(IProgressMonitor monitor,
			IPath workDir, String... cmdLine)
			throws CoreException {
		final ProcessBuilder builder = new ProcessBuilder(cmdLine);
		builder.directory(workDir.toFile());
		if(cmdLine.toString().length() > 30000)
			throw DeeCore.createCoreException(
					"D Build: Error cannot build: cmd-line too big", null);

		try {
			Process proc = builder.start();
			ProcessUtil.waitForProcess(monitor, proc);
			
		} catch (IOException e) {
			throw DeeCore.createCoreException("D Build: Error exec'ing.", e);
		} catch (InterruptedException e) {
			throw DeeCore.createCoreException("D Build: Interrupted.", e);
		}
	}
	
}
