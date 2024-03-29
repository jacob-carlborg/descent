package descent.internal.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.IBuffer;
import descent.core.IClassFile;
import descent.core.IJavaElement;
import descent.core.IJavaModelStatusConstants;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.core.compiler.CharOperation;

/**
 * A working copy on an <code>IClassFile</code>.
 */
public class ClassFileWorkingCopy extends CompilationUnit {
	
	public IClassFile classFile;
	
public ClassFileWorkingCopy(IClassFile classFile, WorkingCopyOwner owner) {
	super((PackageFragment) classFile.getParent(), classFile.getElementName(), owner);
	this.classFile = classFile;
}

public void commitWorkingCopy(boolean force, IProgressMonitor monitor) throws JavaModelException {
	throw new JavaModelException(new JavaModelStatus(IJavaModelStatusConstants.INVALID_ELEMENT_TYPES, this));
}

public IBuffer getBuffer() throws JavaModelException {
	if (isWorkingCopy())
		return super.getBuffer();
	else
		return this.classFile.getBuffer();
}

public char[] getContents() {
	try {
		return getBuffer().getCharacters();
	} catch (JavaModelException e) {
		return CharOperation.NO_CHAR;
	}
}

public IPath getPath() {
	return this.classFile.getPath();
}

public IJavaElement getPrimaryElement(boolean checkOwner) {
	if (checkOwner && isPrimary()) return this;
	return new ClassFileWorkingCopy(this.classFile, DefaultWorkingCopyOwner.PRIMARY);
}

public IResource getResource() {
	return this.classFile.getResource();
}

/**
 * @see Openable#openBuffer(IProgressMonitor, Object)
 */
protected IBuffer openBuffer(IProgressMonitor pm, Object info) throws JavaModelException {

	// create buffer
	IBuffer buffer = this.owner.createBuffer(this);
	if (buffer == null) return null;
	
	// set the buffer source
	if (buffer.getCharacters() == null) {
		IBuffer classFileBuffer = this.classFile.getBuffer();
		if (classFileBuffer != null) {
			buffer.setContents(classFileBuffer.getCharacters());
		} else {
			// Disassemble
			/*
			IClassFileReader reader = ToolFactory.createDefaultClassFileReader(this.classFile, IClassFileReader.ALL);
			Disassembler disassembler = new Disassembler();
			String contents = disassembler.disassemble(reader, Util.getLineSeparator("", getJavaProject()), ClassFileBytesDisassembler.WORKING_COPY); //$NON-NLS-1$
			buffer.setContents(contents);
			*/
		}
	}

	// add buffer to buffer cache
	BufferManager bufManager = getBufferManager();
	bufManager.addBuffer(buffer);
			
	// listen to buffer changes
	buffer.addBufferChangedListener(this);
	
	return buffer;
}

protected void toStringName(StringBuffer buffer) {
	buffer.append(this.classFile.getElementName());
}

}
