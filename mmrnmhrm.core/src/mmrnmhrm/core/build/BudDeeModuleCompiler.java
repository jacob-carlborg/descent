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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.IScriptProject;

public class BudDeeModuleCompiler {
	
	public static BudDeeModuleCompiler getDefault() {
		return new BudDeeModuleCompiler();
	}
	
	public void compileModules(List<IFile> dmodules, IScriptProject deeProj, IProgressMonitor monitor) throws CoreException {
		DeeProjectOptions options = DeeModel.getDeeProjectInfo(deeProj);
		List<String> cmdline = createCommandLine(dmodules, options.compilerOptions);

		IFolder outputFolder = options.getOutputFolder();
		final ProcessBuilder builder = new ProcessBuilder(cmdline);
		builder.directory(outputFolder.getLocation().toFile());

		try {
			Process proc = builder.start();
			ProcessUtil.waitForProcess(monitor, proc);
			
		} catch (IOException e) {
			throw DeeCore.createCoreException("D Build: Error exec'ing.", e);
		} catch (InterruptedException e) {
			throw DeeCore.createCoreException("D Build: Interrupted.", e);
		}

	}

	public static List<String> createCommandLine(List<IFile> dmodules, DeeCompilerOptions options) {
		List<String> cmdline = new ArrayList<String>();
		cmdline.add(options.buildTool);
		for (IFile dmodule : dmodules) {
			cmdline.add(dmodule.getLocation().toOSString());
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
