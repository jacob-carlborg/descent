package mmrnmhrm.core.model;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

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
public class DeeProject extends LangProject {

	private static final String CFG_FILE_NAME = ".deeproject";
	private static final String CFG_FILE_SECTION = "buildpath";
	
	
	private IContainer outputDir; // The resource is allowed to not exist.

	
	public DeeProject(IProject project) {
		super(project);
		this.parent = DeeModelRoot.getInstance();
	}
	
	@Override
	public IDeeSourceRoot[] newChildrenArray(int size) {
		return new IDeeSourceRoot[size];
	}
	
	public IContainer getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(IFolder outputDir) {
		this.outputDir = outputDir;
	}

	/* -------------- ------------------------  -------------- */

	
	public DeeSourceFolder addSourceFolder(IFolder folder) throws CoreException {
		DeeSourceFolder srcFolder = new DeeSourceFolder(folder, this);
		addChild(srcFolder);
		return srcFolder;
	}
	
	public void addSourceFolder(IDeeSourceRoot entry) throws CoreException {
		addChild(entry);
	}
	
	public void removeSourceFolder(IFolder folder) throws CoreException {
		removeChild(new DeeSourceFolder(folder, this));
	}
	
	public void removeSourceFolder(IDeeSourceRoot entry) throws CoreException {
		removeChild(entry);
	}
	

	public IDeeSourceRoot getRoot(IFolder folder) {
		String name = folder.getProjectRelativePath().toString();
		return (IDeeSourceRoot) getLangElement(name);
	}

	
	public IDeeSourceRoot[] getSourceRoots() {
		return (IDeeSourceRoot[]) getChildren();
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<DeeSourceFolder> getSourceFolders() {
		return (ArrayList<DeeSourceFolder>) getChildrenOfType(ELangElementTypes.SOURCEFOLDER);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<DeeSourceLib> getSourceLibs() {
		return (ArrayList<DeeSourceLib>) getChildrenOfType(ELangElementTypes.SOURCELIB);
	}

	/* --------------  persistance -------------- */

	// TODO: sep create and set?
	public void setDefaultBuildPath() throws CoreException {
		// CHECK CREATE
		IFolder srcFolder = project.getFolder("src");
		srcFolder.create(false, true, null);
		addSourceFolder(srcFolder);

		IFolder binFolder = project.getFolder(defaultOutputFolder());
		binFolder.create(false, true, null);
		setOutputDir(binFolder);
	}
	
	
	private String defaultOutputFolder() {
		return "bin";
	}
	
	public void saveProjectConfigFile() throws CoreException {
		IFile projCfgFile = project.getFile(CFG_FILE_NAME);
		
		Ini ini = new Ini();
		Ini.Section section = ini.add(CFG_FILE_SECTION);
		
		int count = 1;
        for(IDeeSourceRoot bpentry : getSourceRoots()) {
        	String path = bpentry.getProjectRelativePath().toPortableString();
        	section.put(bpentry.getSourceRootKindString() + count++, path);
        }
    	section.put("out", getOutputDir().getProjectRelativePath().toString());
		
		StringWriter writer = new StringWriter();
		try {
			ini.store(writer);
		} catch (IOException e) {
			Assert.fail("IOE never happens on a StringWriter");
		}
		
		byte[] buf = writer.getBuffer().toString().getBytes();
		InputStream is = new ByteArrayInputStream(buf);
		if(projCfgFile.exists() == false) {
			projCfgFile.create(is, false, null);
		} else {
			projCfgFile.setContents(is, IResource.NONE, null);
		}

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
				addSourceFolder(folder);
				Logg.println("Added srcfolder:" + folder.toString());
			}
		}
		
		String pathstr = section.get("out");
		if(pathstr == null)
			pathstr = defaultOutputFolder();
		IFolder outFolder = project.getFolder(Path.fromPortableString(pathstr));
		setOutputDir(outFolder);
	}
}
