package mmrnmhrm.core.model.lang;

import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeProject;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;

public abstract class LangModuleUnit extends LangElement {

	protected IFile file;
	protected IDocument document;

	
	public LangModuleUnit(ILangElement parent) {
		super(parent);
	}
	
	public IFile getFile() {
		return file;
	}

	public IResource getUnderlyingResource() {
		return file;
	}

	public String getElementName() {
		return file.getName();
	}

	public int getElementType() {
		return ELangElementTypes.COMPILATION_UNIT;
	}
	
	
	/** Returns the Lang project of this compilation unit, null if none. */
	public DeeProject getProject() {
		return DeeModel.getLangProject(file.getProject().getName());
	}
	
	/** Returns whether this Compilation Unit is out of the model or not
	 * (outside of a build path). */
	public boolean isOutOfModel() {
		return parent == null;
	}
	
	/* -------------- Structure  -------------- */

	@Override
	protected void getElementInfo()  {
		if(!opened) {
			opened = true;
			createElementInfo();
		}
	}
	
	public void createElementInfo() {
		openBuffer();
		parseUnit();
	}
	
	public void disposeElementInfo() throws CoreException {
		closeBuffer();
	}

	private void closeBuffer() throws CoreException {
		if(document == null)
			return;
		
		ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
		IPath loc = file.getFullPath();
		LocationKind fLocationKind = LocationKind.IFILE;

		manager.disconnect(loc, fLocationKind, null);
	}

	protected void openBuffer()  {
		if(document != null)
			return;
		
		ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
		IPath loc = file.getFullPath();
		LocationKind fLocationKind = LocationKind.IFILE;

		ITextFileBuffer textFileBuffer;
		try {
			// XXX: do disconnect in some sort of dispose
			manager.connect(loc, fLocationKind, null);
			//manager.connect(loc, fLocationKind, new NullProgressMonitor());
			textFileBuffer = manager.getTextFileBuffer(loc, fLocationKind);
			document = textFileBuffer.getDocument();
		} catch (CoreException ce) {
			//fStatus= x.getStatus();
			document = manager.createEmptyDocument(loc, fLocationKind);
		}
	}
	

	public IDocument getDocument() {
		openBuffer();
		return document;
	}
	
	public String getSource() {
		return getDocument().get();
	}
	
	public abstract void reconcile();

	protected abstract void parseUnit();
}