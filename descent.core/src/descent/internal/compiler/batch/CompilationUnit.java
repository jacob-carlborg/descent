package descent.internal.compiler.batch;

import java.io.File;
import java.io.IOException;

import descent.core.compiler.CharOperation;
import descent.internal.compiler.env.ICompilationUnit;
import descent.internal.compiler.problem.AbortCompilationUnit;
import descent.internal.compiler.util.Util;

public class CompilationUnit implements ICompilationUnit {
	public char[] contents;
	public char[] fileName;
	public char[] mainTypeName;
	String encoding;
	
public CompilationUnit(char[] contents, String fileName, String encoding) {
	this.contents = contents;
	char[] fileNameCharArray = fileName.toCharArray();
	switch(File.separatorChar) {
		case '/' :
			if (CharOperation.indexOf('\\', fileNameCharArray) != -1) {
				CharOperation.replace(fileNameCharArray, '\\', '/');
			}
			break;
		case '\\' :
			if (CharOperation.indexOf('/', fileNameCharArray) != -1) {
				CharOperation.replace(fileNameCharArray, '/', '\\');
			}
	}
	this.fileName = fileNameCharArray;
	int start = CharOperation.lastIndexOf(File.separatorChar, fileNameCharArray) + 1; 

	int end = CharOperation.lastIndexOf('.', fileNameCharArray);
	if (end == -1) {
		end = fileNameCharArray.length;
	}

	this.mainTypeName = CharOperation.subarray(fileNameCharArray, start, end);
	this.encoding = encoding;
}
public char[] getContents() {
	if (this.contents != null)
		return this.contents;   // answer the cached source

	// otherwise retrieve it
	try {
		return Util.getFileCharContent(new File(new String(this.fileName)), this.encoding);
	} catch (IOException e) {
		this.contents = CharOperation.NO_CHAR; // assume no source if asked again
		throw new AbortCompilationUnit(null, e, this.encoding);
	}
}
/**
 * @see descent.internal.compiler.env.IDependent#getFileName()
 */
public char[] getFileName() {
	return this.fileName;
}
public char[] getMainTypeName() {
	return this.mainTypeName;
}
public char[][] getPackageName() {
	return null;
}
public String getFullyQualifiedName() {
	return null;
}
public String toString() {
	return "CompilationUnit[" + new String(this.fileName) + "]";  //$NON-NLS-2$ //$NON-NLS-1$
}
}
