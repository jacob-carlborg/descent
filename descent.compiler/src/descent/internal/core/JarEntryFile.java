package descent.internal.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;

import descent.core.IJavaModelStatusConstants;
import descent.core.JavaModelException;

/**
 * A jar entry that represents a non-java resource found in a JAR.
 *
 * @see IStorage
 */
public class JarEntryFile extends PlatformObject implements IStorage {
	private String entryName;
	private String zipName;
	private IPath path;
	
	public JarEntryFile(String entryName, String zipName){
		this.entryName = entryName;
		this.zipName = zipName;
		this.path = new Path(this.entryName);
	}
public InputStream getContents() throws CoreException {

	try {
		File file = new File(zipName, entryName);
		return new FileInputStream(file);
	} catch (IOException e) {
		throw new JavaModelException(e, IJavaModelStatusConstants.IO_EXCEPTION);
	}
}
/**
 * @see IStorage#getFullPath
 */
public IPath getFullPath() {
	return this.path;
}
/**
 * @see IStorage#getName
 */
public String getName() {
	return this.path.lastSegment();
}
/**
 * @see IStorage#isReadOnly()
 */
public boolean isReadOnly() {
	return true;
}
/**
 * @see IStorage#isReadOnly()
 */
public String toString() {
	return "JarEntryFile["+this.zipName+"::"+this.entryName+"]"; //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-1$
}
}
