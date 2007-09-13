package mmrnmhrm.core.model;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.build.DeeCompilerOptions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IScriptProject;
import org.ini4j.Ini;
import org.ini4j.InvalidIniFormatException;

import dtool.Logg;

import static melnorme.miscutil.Assert.assertFail;


public class DeeProjectOptions {

	public static final String CFG_FILE_NAME = ".dprojectoptions";

	private static final String CFG_FILE_SECTION = "compileoptions";

	public final IScriptProject dltkProj;

	public DeeCompilerOptions compilerOptions;
	
	protected DeeProjectOptions(IScriptProject dltkProj) {
		this.dltkProj = dltkProj;
		this.compilerOptions = new DeeCompilerOptions(dltkProj.getElementName());
	}
	
	public IProject getProject() {
		return dltkProj.getProject();
	}
	
	public IFolder getOutputFolder() {
		return getProject().getFolder(compilerOptions.outputDir); 
	}
	

	
	public void saveProjectConfigFile() throws CoreException {
		Ini ini = new Ini();
		Ini.Section section = ini.add(CFG_FILE_SECTION);
		
    	section.put("out", getOutputFolder().getProjectRelativePath().toString());
    	section.put("buildtype", compilerOptions.buildType.toString());
    	section.put("extraOptions", compilerOptions.extraOptions);
		
		writeConfigFile(ini);
	}

	/** Loads a project config if one exists. If one doesn't 
	 * exist already, create one with defaults settings. */
	public void loadNewProjectConfig() throws CoreException {
		IFile projCfgFile = getProject().getFile(CFG_FILE_NAME);
		if(projCfgFile.exists()) {
			loadProjectConfigFile();
		} else {
			saveProjectConfigFile();
		}
	}

	public void loadProjectConfigFile() throws CoreException {
		Ini ini = readConfigFile();
		
		Ini.Section section = ini.get(CFG_FILE_SECTION);
		if(section == null) {
			// Proceed as if it did exist
			section = ini.add(CFG_FILE_SECTION);
		}
		
		String pathstr = section.get("out");
		if(pathstr != null)
			compilerOptions.outputDir = Path.fromPortableString(pathstr);
	}


	private Ini readConfigFile() throws CoreException {
		IFile projCfgFile = getProject().getFile(CFG_FILE_NAME);
		Logg.main.println(projCfgFile.getLocationURI());

		Ini ini = new Ini();
 
		try {
			if(projCfgFile.exists()) {
				ini.load(projCfgFile.getContents());
			}
		} catch (InvalidIniFormatException e) {
			throw DeeCore.createCoreException("Error loading project file.", e); 
		} catch (FileNotFoundException e) {
			throw DeeCore.createCoreException("Error loading project file.", e); 
		} catch (IOException e) {
			throw DeeCore.createCoreException("Error loading project file.", e); 
		}
		return ini;
	}


	private void writeConfigFile(Ini ini) throws CoreException {
		StringWriter writer = new StringWriter();
		try {
			ini.store(writer);
		} catch (IOException e) {
			assertFail("IOE never happens on a StringWriter");
		}
		
		IFile projCfgFile = getProject().getFile(CFG_FILE_NAME);
		byte[] buf = writer.getBuffer().toString().getBytes();
		InputStream is = new ByteArrayInputStream(buf);
		if(projCfgFile.exists() == false) {
			projCfgFile.create(is, false, null);
		} else {
			projCfgFile.setContents(is, IResource.NONE, null);
		}
	}
}
