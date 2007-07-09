package mmrnmhrm.core.build;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.ExceptionAdapter;
import melnorme.miscutil.FileUtil;
import melnorme.miscutil.StringUtil;
import melnorme.miscutil.log.Logg;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.DeeSourceFolder;
import mmrnmhrm.core.model.IDeeSourceRoot;
import mmrnmhrm.core.model.lang.LangSourceFolder;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * Compiles a project in a /certain/ D compile environment  
 *
 */
public class DMDCompilerEnviron implements IDeeCompilerEnviron {
	
	private DeeProject deeProject;

	public DMDCompilerEnviron(DeeProject project) {
		this.deeProject = project;
	}
	
	private void prepOutputDir() throws CoreException {
		// Does not support project location as output dir
		IFolder outputDir = (IFolder) deeProject.getOutputDir();
		if(!outputDir.exists())
			outputDir.create(false, true, null);
		IResource[] oldResources = outputDir.members();
		DeeCore.getWorkspace().delete(oldResources, false, null);

		IPath outputPath = outputDir.getFullPath();

		for(IDeeSourceRoot bpentry : deeProject.getSourceRoots()) {
			if(bpentry instanceof DeeSourceFolder) {
				LangSourceFolder dsf = (LangSourceFolder) bpentry;
				IResource[] resources = new IResource[]{ dsf.folder };
				DeeCore.getWorkspace().copy(resources, outputPath, 0, null);
			}
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

		File file = new File(deeProject.getOutputDir().getLocation().toString());
		Logg.builder.println(StringUtil.collToString(cmdstr, "\n "));

		Process process = null;
		try {
			//process = builder.start();
			process = Runtime.getRuntime().exec(cmdstr, null, file);
		} catch (IOException e) {
			throw ExceptionAdapter.unchecked(e);
		} finally {
			deeProject.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
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
