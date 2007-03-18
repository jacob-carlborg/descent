package mmrnmhrm.core.model;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import mmrnmhrm.core.DeeCoreException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.ini4j.Ini;
import org.ini4j.InvalidIniFormatException;

import util.Assert;
import util.log.Logg;


/**
 * Class for a D project. 
 */
public class DeeProject {

	private static final String CFG_FILE_NAME = ".deeproject";
	private static final String CFG_FILE_SECTION = "buildpath";
	
	
	private IProject project;
	private List<IBuildPathEntry> buildpath;
	private IContainer outputDir; // Allowed to not exist.

	
	public DeeProject() {
		buildpath = new ArrayList<IBuildPathEntry>();
	}
	
	public IProject getProject() {
		return project;
	}
	
	public void setProject(IProject project) {
		this.project = project;
	}
	
	public IContainer getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(IFolder outputDir) {
		this.outputDir = outputDir;
		//saveProjectConfigFile();
	}
	
	public List<IBuildPathEntry> getSourceFolders() {
		return buildpath;
	}

	
	public String toString() {
		return project.getName();
	}

	public String getOutputDirLocationString() {
		return outputDir.getLocation().toString();
	}

	/* -------------- ------------------------  -------------- */

	
	public void addSourceFolder(IFolder folder) throws CoreException {
		buildpath.add(new DeeSourceFolder(folder));
		//saveProjectConfigFile();
	}
	
	public void removeSourceFolder(IFolder folder) throws CoreException {
		buildpath.remove(new DeeSourceFolder(folder));
		//saveProjectConfigFile();
	}

	public void saveProjectConfigFile() throws CoreException {
		IFile projCfgFile = project.getFile(CFG_FILE_NAME);
		
		Ini ini = new Ini();
		Ini.Section section = ini.add(CFG_FILE_SECTION);
		
		int count = 1;
        for(IBuildPathEntry bpentry : buildpath) {
        	String path = bpentry.getPathString();
        	section.put(bpentry.getKindString() + count++, path);
        }
    	section.put("out", getOutputDir().getProjectRelativePath().toString());
		
		StringWriter writer = new StringWriter();
		try {
			ini.store(writer);
		} catch (IOException e) {
			Assert.fail("Never happens a IOE on a StringWriter");
		}
		
		byte[] buf = writer.getBuffer().toString().getBytes();
		InputStream is = new ByteArrayInputStream(buf);
		if(projCfgFile.exists() == false) {
			projCfgFile.create(is, false, null);
		} else {
			projCfgFile.setContents(is, IResource.NONE, null);
		}

	}
	
	private String defaultOutputFolder() {
		return "bin";
	}

	public void loadProjectConfigFile() throws CoreException {
		IFile projCfgFile = project.getFile(CFG_FILE_NAME);
		Logg.println(projCfgFile.getLocationURI());

		Ini ini = new Ini();
 
		try {
			if(projCfgFile.exists()) {
				ini.load(projCfgFile.getContents());
			}
		} catch (InvalidIniFormatException e) {
			throw new DeeCoreException("Error loading project file.", e); 
		} catch (FileNotFoundException e) {
			throw new DeeCoreException("Error loading project file.", e); 
		} catch (IOException e) {
			throw new DeeCoreException("Error loading project file.", e); 
		}
		
		Ini.Section section = ini.get(CFG_FILE_SECTION);
		if(section == null) {
			// Proceed as if it did exist
			section = ini.add(CFG_FILE_SECTION);
		}
		
		for(String key : section.keySet()) {
			if(key.startsWith("src")) {
				IPath path = Path.fromPortableString(section.get(key));
				IFolder folder = project.getFolder(path);
				if (folder.exists() == false) {
					Logg.err.println("Error: src folder does not exist:"
							+ folder.toString());
					// TODO: problemize
					continue;
				}
				buildpath.add(new DeeSourceFolder(folder));
				Logg.println("Added srcfolder:" + folder.toString());
			}
		}
		
		String pathstr = section.get("out");
		if(pathstr == null)
			pathstr = defaultOutputFolder();
		IFolder outFolder = project.getFolder(Path.fromPortableString(pathstr));
		setOutputDir(outFolder);
	}
	


	public IBuildPathEntry getEntry(IPath element) {
		for(IBuildPathEntry bpentry : buildpath) {
			if(bpentry.getProjectRelativePath().equals(element))
				return bpentry;
		}
		return null;
	}


	public void setDefaultBuildPath() throws CoreException {
		// CHECK CREATE
		IFolder srcFolder = project.getFolder("src");
		srcFolder.create(false, true, null);
		addSourceFolder(srcFolder);

		IFolder binFolder = project.getFolder(defaultOutputFolder());
		binFolder.create(false, true, null);
		setOutputDir(binFolder);
	}


}
