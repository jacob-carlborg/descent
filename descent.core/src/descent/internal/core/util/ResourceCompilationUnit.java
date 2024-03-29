package descent.internal.core.util;

import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import descent.core.compiler.CharOperation;
import descent.internal.compiler.batch.CompilationUnit;

/**
 * An ICompilationUnit that retrieves its contents using an IFile
 */
public class ResourceCompilationUnit extends CompilationUnit {
	
	private IFile file;
	
	public ResourceCompilationUnit(IFile file, URI location) {
		super(null/*no contents*/, location == null ? file.getFullPath().toString() : location.getPath(), null/*encoding is used only when retrieving the contents*/);
		this.file = file;
	}

	public char[] getContents() {
		if (this.contents != null)
			return this.contents;   // answer the cached source
	
		// otherwise retrieve it
		try {
			return Util.getResourceContentsAsCharArray(this.file);
		} catch (CoreException e) {
			return CharOperation.NO_CHAR;
		}
	}
}
