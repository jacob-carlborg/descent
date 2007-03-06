package mmrnmhrm.core.build;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeProject0;
import mmrnmhrm.core.model.DeeSourceFolder;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import util.ExceptionAdapter;
import util.FileUtil;
import util.Logg;
import util.StringUtil;
/**
 * Compiles a project in a /certain/ D compile environment  
 *
 */
public class DMDCompilerEnviron implements IDeeCompilerEnviron {
	
	private DeeProject0 deeProject;

	public DMDCompilerEnviron(DeeProject0 project) {
		this.deeProject = project;
	}
	
	private void prepOutputDir() throws CoreException {
		IPath outputPath = deeProject.outputDir.getFullPath();
		IResource[] oldResources = deeProject.outputDir.members();
		DeeCore.getWorkspace().delete(oldResources, false, null);

		for(DeeSourceFolder dsf : deeProject.sourceFolders) {
			//dsf.folder.copy(outputPath, IResource.NONE, null);
			IResource[] resources = new IResource[]{ dsf.folder };
			DeeCore.getWorkspace().copy(resources, outputPath, 0, null);
		}
	}
	
	public void compileModules(List<IResource> dmodules) throws CoreException {
		
		prepOutputDir();
		
		List<String> command = new ArrayList<String>();
		command.add("dmd");

		//ProcessBuilder builder = new ProcessBuilder(command);
		// builder.directory(getProject());

		int numOptions = 2;
		String[] cmdstr = new String[1 + numOptions + dmodules.size()];
		cmdstr[0] = "dmd";
		cmdstr[1] = "-of" + deeProject.toString() + ".exe";
		cmdstr[2] = "-op";
		//cmdstr[2] = "-od" + deeProject.getOutputDirLocationString();
		int i = 1 + numOptions;
		for(IResource dmodule : dmodules) {
			IPath srcDirPath = dmodule.getFullPath().removeFirstSegments(1);
			cmdstr[i] = srcDirPath.toString();
			i++;
		}

		File file = new File(deeProject.getOutputDirLocationString());
		Logg.println( StringUtil.collToString(cmdstr, "\n "));

		Process process = null;
		try {
			//process = builder.start();
			process = Runtime.getRuntime().exec(cmdstr, null, file);
		} catch (IOException e) {
			throw ExceptionAdapter.unchecked(e);
		} finally {
			deeProject.project.refreshLocal(IResource.DEPTH_INFINITE, null);
		}

		// Capture Output
		InputStreamReader isr = new InputStreamReader(process.getInputStream());
		try {
			System.out.println(FileUtil.readStringFromReader(isr));
		} catch (IOException e) {
			throw ExceptionAdapter.unchecked(e);
		}
	}


}
