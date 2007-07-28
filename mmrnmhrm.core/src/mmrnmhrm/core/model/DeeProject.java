package mmrnmhrm.core.model;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.naming.ldap.SortControl;

import melnorme.miscutil.Assert;
import melnorme.miscutil.log.Logg;
import mmrnmhrm.core.DeeCoreException;
import mmrnmhrm.core.build.DeeCompilerOptions;
import mmrnmhrm.core.model.lang.ELangElementTypes;
import mmrnmhrm.core.model.lang.ILangElement;
import mmrnmhrm.core.model.lang.LangProject;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.ini4j.Ini;
import org.ini4j.InvalidIniFormatException;


/**
 * Class for a D project. 
 */
public class DeeProject extends LangProject implements IDeeElement {

	private static final String CFG_FILE_NAME = ".deeproject";
	private static final String CFG_FILE_SECTION = "buildpath";
	
	

	public DeeCompilerOptions compilerOptions;
	
	public DeeProject(IProject project) {
		super(DeeModelRoot.getInstance(), project);
		this.compilerOptions = new DeeCompilerOptions();
	}
	
	@Override
	public IDeeSourceRoot[] newChildrenArray(int size) {
		return new IDeeSourceRoot[size];
	}
	

	/* -------------- Structure  -------------- */
	
	/** {@inheritDoc} */
	public void createStructure() throws CoreException {
		opened = true;
		loadProjectConfigFile();
	}
	
	/** {@inheritDoc} */ @Override
	public void updateElem() throws CoreException {
		updateErrorMarkers();
	}

	private void updateErrorMarkers() throws CoreException {
		project.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
		for(IDeeSourceRoot sourceRoot : getSourceRoots()) {
			if(sourceRoot.getUnderlyingResource().exists() == false) {
				createMissingMarker(sourceRoot);
			}
		}
	}
	
	private void createMissingMarker(IDeeSourceRoot sourceRoot) throws CoreException {
		IMarker marker =project.createMarker(IMarker.PROBLEM);
		marker.setAttribute(IMarker.MESSAGE, 
				DeeCoreMessages.BUILD_PATH_RESOURCE_MISSING + sourceRoot.getUnderlyingResource());
		marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
	}


	

	/* =====================  persistence ===================== */

	// TODO: sep create and set?
	public void setDefaultBuildPath() throws CoreException {
		// CHECK CREATE
		IFolder srcFolder = project.getFolder("src");
		srcFolder.create(false, true, null);
		createAddSourceFolder(srcFolder);

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
		Logg.main.println(projCfgFile.getLocationURI());

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
		
		setChildren(newChildrenArray(0)); // reset children
		
		for(String key : section.keySet()) {
			if(key.startsWith("src")) {
				IPath path = Path.fromPortableString(section.get(key));
				IFolder folder = project.getFolder(path);
				if (folder.exists() == false) {
					Logg.model.println("Error: src folder does not exist:"
							+ folder.toString());
					// TODO: problemize
					continue;
				}
				createAddSourceFolder(folder);
				Logg.model.println("Added srcfolder:" + folder.toString());
			}
		}
		
		String pathstr = section.get("out");
		if(pathstr == null)
			pathstr = defaultOutputFolder();
		IFolder outFolder = project.getFolder(Path.fromPortableString(pathstr));
		setOutputDir(outFolder);
	}

}
