package mmrnmhrm.core.model.lang;


import java.util.ArrayList;

import mmrnmhrm.core.model.DeeSourceFolder;
import mmrnmhrm.core.model.DeeSourceLib;
import mmrnmhrm.core.model.IDeeSourceRoot;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;

public abstract class LangProject extends LangContainerElement implements ILangProject {

	protected IProject project;
	protected IScriptProject dltkProj;
	
	protected IContainer outputDir; // The resource is allowed to not exist.


	
	public LangProject(LangContainerElement parent, IScriptProject dltkProj) {
		super(parent);
		this.dltkProj = dltkProj;
		this.project = dltkProj.getProject();
	}
	
	
	public IProject getProject() {
		return project;
	}
	
	public String getElementName() {
		return project.getName();
	}

	public int getElementType() {
		return ELangElementTypes.PROJECT;
	}
	
	/** {@inheritDoc} */
	public IResource getUnderlyingResource() {
		return project;
	}
	
	/** Gets the output dir path for this project. 
	 * The resource is allowed not to exist. */
	public IContainer getOutputDir() {
		return outputDir;
	}
	
	/** Sets the output dir path for this project. 
	 * The resource is allowed not to exist. */
	public void setOutputDir(IFolder outputDir) {
		this.outputDir = outputDir;
	}

	
	/** Adds a SourceRoot to this project. */
	public void addSourceRoot(IDeeSourceRoot entry) throws CoreException {
		getElementInfo();
		//IFolder resource = (IFolder) entry.getUnderlyingResource();
		//LangSourceFolder.createSrcFolderEntry(dltkProj.getProjectFragment(resource));
		//IModelStatus status = BuildpathEntry.validateBuildpath(dltkProj, new IBuildpathEntry[0]);
		//Assert.isTrue(status.getSeverity() == IStatus.OK);
		dltkProj.setRawBuildpath(new IBuildpathEntry[0], null);
		//fragment.open(null);
		addChild(entry);
		//entry.updateElementRecursive();
	}



	/** Removes a SourceRoot to this project. 
	 * Does not delete underlying resource. */
	public void removeSourceRoot(IDeeSourceRoot entry) throws CoreException {
		getElementInfo();
		removeChild(entry);
	}
	
	/** Gets the SourceRoot for the given folder, null if not found. */
	public IDeeSourceRoot getSourceRoot(IFolder folder) throws CoreException {
		getElementInfo();
		for (IDeeSourceRoot element : getSourceRoots()) {
			if(element.getUnderlyingResource().equals(folder))
				return element;
		}
		return null;
	}

	/** Gets all SourceRoots. */
	public IDeeSourceRoot[] getSourceRoots() throws CoreException {
		getElementInfo();
		return (IDeeSourceRoot[]) getChildren();
	}
	
	/** Gets the Source Roots of type Source Folder. */
	@SuppressWarnings("unchecked")
	public DeeSourceFolder[] getSourceFolders() throws CoreException {
		getElementInfo();
		return getChildrenOfType(ELangElementTypes.SOURCEFOLDER).toArray(
				DeeSourceFolder.NO_ELEMENTS);
	}

	/** Gets the Source Roots of type Source Library. */
	@SuppressWarnings("unchecked")
	public ArrayList<DeeSourceLib> getSourceLibraries() throws CoreException {
		getElementInfo();
		return (ArrayList<DeeSourceLib>) getChildrenOfType(ELangElementTypes.SOURCELIB);
	}
	
	/** Creates a SourceFolder for the given folder, and adds it the project. */
	public DeeSourceFolder createAddSourceFolder(IFolder folder) throws CoreException {
		DeeSourceFolder srcFolder = new DeeSourceFolder(folder, this);
		addSourceRoot(srcFolder);
		return srcFolder;
	}


}
