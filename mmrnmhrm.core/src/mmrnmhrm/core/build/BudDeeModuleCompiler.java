package mmrnmhrm.core.build;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.LangCore;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeProjectOptions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
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
		Logg.main.println(">>> " + cmdline);
		DeeBuilder.buildListener.clear();
		DeeBuilder.buildListener.println(">>> " + cmdline);
		String flatCmdLine = cmdline.toString();
		Logg.main.println(">>> " + flatCmdLine.length());
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
		
		getModulesString(dmodules, deeProj, options, cmdline);
		
		cmdline.add("-Rn");
		String appname;
		appname = options.artifactName;
		cmdline.add("-T"+appname);
		String[] extrasOpts = options.extraOptions.split("\n");
		for (int i = 0; i < extrasOpts.length; i++) {
			cmdline.add(extrasOpts[i]);
		}
		return cmdline;
	}

	private static void getModulesString(List<IFile> dmodules,
			IScriptProject deeProj, DeeCompilerOptions options,
			List<String> cmdline) {

		IPath outputDirPath = deeProj.getProject().getFullPath();
		outputDirPath = outputDirPath.append(options.outputDir);

		
		if(dmodules == null) {
			dmodules = new ArrayList<IFile>();
			IPath examplepath = outputDirPath.append("<files.d>");
			IFile file = LangCore.getWorkspaceRoot().getFile(examplepath);
			dmodules.add(file);
		}
		
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
	}


}
