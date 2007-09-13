package mmrnmhrm.core.build;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeProjectOptions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.IScriptProject;

import dtool.Logg;

public class BudDeeModuleCompiler {
	
	public static BudDeeModuleCompiler getDefault() {
		return new BudDeeModuleCompiler();
	}
	
	public void compileModules(List<IFile> dmodules, IScriptProject deeProj, IProgressMonitor monitor) throws CoreException {
		DeeProjectOptions options = DeeModel.getDeeProjectInfo(deeProj);
		List<String> cmdline = createCommandLine(dmodules, deeProj, options.compilerOptions);

		IFolder outputFolder = options.getOutputFolder();
		final ProcessBuilder builder = new ProcessBuilder(cmdline);
		builder.directory(outputFolder.getLocation().toFile());
		Logg.main.println("»»» " + cmdline);
		DeeBuilder.buildListener.println("»»» " + cmdline);
		String flatCmdLine = cmdline.toString();
		Logg.main.println("»»» " + flatCmdLine.length());
		if(flatCmdLine.length() > 30000)
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
	

	public static List<String> createCommandLine(List<IFile> dmodules, 
			IScriptProject deeProj, DeeCompilerOptions options) {
		List<String> cmdline = new ArrayList<String>();
		cmdline.add(options.buildTool);
		IPath outputDirPath = deeProj.getProject().getFullPath();
		outputDirPath = outputDirPath.append(options.outputDir);
		
		for (IFile dmodule : dmodules) {
			IPath path = dmodule.getFullPath();
			int matching = path.matchingFirstSegments(outputDirPath);
			/*if(outputDirPath.getDevice() == null
				|| outputDirPath.getDevice().equals(path.getDevice())) {
				path.setDevice(null);
				path = path.removeFirstSegments(matching);
			}*/
			path = path.removeFirstSegments(matching);
			cmdline.add(path.toOSString());
		}
		cmdline.add("-Rn");
		String appname;
		appname = options.artifactName + getPlatformExecutableExtension();
		cmdline.add("-T"+appname);
		cmdline.add(options.extraOptions);
		return cmdline;
	}

	private static String getPlatformExecutableExtension() {
		if(Platform.getOS().equals(Platform.OS_WIN32))
			return ".exe";
		return "";
	}

}
