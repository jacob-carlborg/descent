package descent.internal.core;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import descent.core.IBuffer;
import descent.core.IClassFile;
import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IMember;
import descent.core.IPackageFragmentRoot;
import descent.core.IProblemRequestor;
import descent.core.ISourceRange;
import descent.core.ISourceReference;
import descent.core.JavaModelException;
import descent.core.Signature;
import descent.core.WorkingCopyOwner;
import descent.internal.compiler.util.SuffixConstants;
import descent.internal.core.util.Util;

/**
 * @see IClassFile
 */

public class ClassFile extends CompilationUnit implements IClassFile, SuffixConstants {

/*
 * Creates a handle to a class file.
 */
protected ClassFile(PackageFragment parent, String name) {
	super(parent, name, DefaultWorkingCopyOwner.PRIMARY);
}

/*
 * @see IClassFile#becomeWorkingCopy(IProblemRequestor, WorkingCopyOwner, IProgressMonitor)
 */
public ICompilationUnit becomeWorkingCopy(IProblemRequestor problemRequestor, WorkingCopyOwner owner, IProgressMonitor monitor) throws JavaModelException {
	JavaModelManager manager = JavaModelManager.getJavaModelManager();
	CompilationUnit workingCopy = new ClassFileWorkingCopy(this, owner == null ? DefaultWorkingCopyOwner.PRIMARY : owner);
	JavaModelManager.PerWorkingCopyInfo perWorkingCopyInfo = manager.getPerWorkingCopyInfo(workingCopy, false/*don't create*/, true /*record usage*/, null/*no problem requestor needed*/);
	if (perWorkingCopyInfo == null) {
		// close cu and its children
		close();

		BecomeWorkingCopyOperation operation = new BecomeWorkingCopyOperation(workingCopy, problemRequestor);
		operation.runOperation(monitor);
		
		return workingCopy;
	}
	return perWorkingCopyInfo.workingCopy;
}

/**
 * Creates the children elements for this class file adding the resulting
 * new handles and info objects to the newElements table. Returns true
 * if successful, or false if an error is encountered parsing the class file.
 * 
 * @see Openable
 * @see Signature
 */
/*
protected boolean buildStructure(OpenableElementInfo info, IProgressMonitor pm, Map newElements, IResource underlyingResource) throws JavaModelException {
	// check whether the class file can be opened
	IStatus status = validateClassFile();
	if (!status.isOK()) throw newJavaModelException(status);
	if (underlyingResource != null && !underlyingResource.isAccessible()) throw newNotPresentException();

	IBinaryType typeInfo = getBinaryTypeInfo((IFile) underlyingResource);
	if (typeInfo == null) {
		// The structure of a class file is unknown if a class file format errors occurred
		//during the creation of the diet class file representative of this ClassFile.
		info.setChildren(new IJavaElement[] {});
		return false;
	}

	// Make the type
	IType type = new BinaryType(this, simpleName(typeInfo.getName()));
	info.setChildren(new IJavaElement[] {type});
	newElements.put(type, typeInfo);
	return true;
}
*/
@Override
protected IBuffer openBuffer(IProgressMonitor pm, Object info) throws JavaModelException {
	char[] contents;
	
	File file = getPath().toFile();
	if (file.exists() && !file.isDirectory()) {
		contents = getContents(file);
	} else {
		contents = new char[0];
	}
	
	// create buffer
	IBuffer buffer = getBufferManager().createBuffer(this);
	if (buffer == null) return null;
	BufferManager bufManager = getBufferManager();
	bufManager.addBuffer(buffer);
	
	// set the buffer source
	if (buffer.getCharacters() == null){
		buffer.setContents(contents);
	}
	
	// listen to buffer changes
	buffer.addBufferChangedListener(this);	
	
	return buffer;
}

private char[] getContents(File file) {
	try {
		char[] contents = new char[(int) file.length()];
		FileReader fr = new FileReader(file);
		fr.read(contents, 0, (int) file.length());
		return contents;
	} catch (IOException e) {
		return new char[0];
	}
}

@Override
protected IStatus validateCompilationUnit(IResource resource) {
	return Status.OK_STATUS;
}
/**
 * @see ICodeAssist#codeComplete(int, ICompletionRequestor)
 * @deprecated
 */
/*
public void codeComplete(int offset, ICompletionRequestor requestor) throws JavaModelException {
	codeComplete(offset, requestor, DefaultWorkingCopyOwner.PRIMARY);
}
*/
/**
 * @see ICodeAssist#codeComplete(int, ICompletionRequestor, WorkingCopyOwner)
 * @deprecated
 */
/*
public void codeComplete(int offset, ICompletionRequestor requestor, WorkingCopyOwner owner) throws JavaModelException {
	if (requestor == null) {
		throw new IllegalArgumentException("Completion requestor cannot be null"); //$NON-NLS-1$
	}
	codeComplete(offset, new descent.internal.codeassist.CompletionRequestorWrapper(requestor), owner);
}
*/

/* (non-Javadoc)
 * @see descent.core.ICodeAssist#codeComplete(int, descent.core.CompletionRequestor)
 */
/*
public void codeComplete(int offset, CompletionRequestor requestor) throws JavaModelException {
	codeComplete(offset, requestor, DefaultWorkingCopyOwner.PRIMARY);
}
*/

/* (non-Javadoc)
 * @see descent.core.ICodeAssist#codeComplete(int, descent.core.CompletionRequestor, descent.core.WorkingCopyOwner)
 */
/*
public void codeComplete(int offset, CompletionRequestor requestor, WorkingCopyOwner owner) throws JavaModelException {
	String source = getSource();
	if (source != null) {
		BinaryType type = (BinaryType) getType();
		BasicCompilationUnit cu = 
			new BasicCompilationUnit(
				getSource().toCharArray(), 
				null,
				type.sourceFileName((IBinaryType) type.getElementInfo()),
				getJavaProject()); // use project to retrieve corresponding .java IFile
		codeComplete(cu, cu, offset, requestor, owner);
	}
}
*/

/**
 * @see ICodeAssist#codeSelect(int, int)
 */
/*
public IJavaElement[] codeSelect(int offset, int length) throws JavaModelException {
	return codeSelect(offset, length, DefaultWorkingCopyOwner.PRIMARY);
}
*/
/**
 * @see ICodeAssist#codeSelect(int, int, WorkingCopyOwner)
 */
/*
public IJavaElement[] codeSelect(int offset, int length, WorkingCopyOwner owner) throws JavaModelException {
	IBuffer buffer = getBuffer();
	char[] contents;
	if (buffer != null && (contents = buffer.getCharacters()) != null) {
	    BinaryType type = (BinaryType) getType();
		BasicCompilationUnit cu = new BasicCompilationUnit(contents, null, type.sourceFileName((IBinaryType) type.getElementInfo()));
		return super.codeSelect(cu, offset, length, owner);
	} else {
		//has no associated souce
		return new IJavaElement[] {};
	}
}
*/
/**
 * Returns a new element info for this element.
 */
/*
protected Object createElementInfo() {
	return new ClassFileInfo(this);
}
*/
public boolean equals(Object o) {
	if (!(o instanceof ClassFile)) return false;
	ClassFile other = (ClassFile) o;
	return this.name.equals(other.name) && this.parent.equals(other.parent);
}
public boolean exists() {
	//return super.exists() && validateClassFile().isOK();
	//return true;
//	if (getResource() != null) {
//		return getResource().getLocation().toFile().exists();
//	} else 
		if (getPath() != null) {
		return getPath().toFile().exists();
	} else {
		return false;
	}
}
public String getAttachedJavadoc(IProgressMonitor monitor) throws JavaModelException {
	//return this.getType().getAttachedJavadoc(monitor);
	return null;
}
/*
public IBuffer getBuffer() throws JavaModelException {
	IStatus status = validateClassFile();
	if (status.isOK()) {
		return super.getBuffer();
	} else {
		// .class file not on classpath, create a new buffer to be nice (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=41444)
		Object info = ((ClassFile) getClassFile()).getBinaryTypeInfo((IFile) getResource());
		IBuffer buffer = openBuffer(null, info);
		if (buffer != null)
			return buffer;
		if (status.getCode() == IJavaModelStatusConstants.ELEMENT_NOT_ON_CLASSPATH)
			return null; // don't throw a JavaModelException to be able to open .class file outside the classpath (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=138507)
		throw new JavaModelException((IJavaModelStatus) status);
	}
}
*/
/**
 * @see IMember
 */
public IClassFile getClassFile() {
	return this;
}
/**
 * A class file has a corresponding resource unless it is contained
 * in a jar.
 *
 * @see IJavaElement
 */
public IResource getCorrespondingResource() throws JavaModelException {
	IPackageFragmentRoot root= (IPackageFragmentRoot)getParent().getParent();
	if (root.isArchive()) {
		return null;
	} else {
		return getUnderlyingResource();
	}
}
/**
 * @see IClassFile
 */
public IJavaElement getElementAt(int position) throws JavaModelException {
	IJavaElement e= getSourceElementAt(position);
	if (e == this) {
		return null;
	} else {
		return e;
	}
}
public String getElementName() {
	return this.name;
}
/**
 * @see IJavaElement
 */
public int getElementType() {
	return CLASS_FILE;
}
/*
 * @see JavaElement
 */
/*
public IJavaElement getHandleFromMemento(String token, MementoTokenizer memento, WorkingCopyOwner owner) {
	switch (token.charAt(0)) {
		case JEM_TYPE:
			if (!memento.hasMoreTokens()) return this;
			String typeName = memento.nextToken();
			JavaElement type = new BinaryType(this, typeName);
			return type.getHandleFromMemento(memento, owner);
	}
	return null;
}
*/
/**
 * @see JavaElement#getHandleMemento()
 */
protected char getHandleMementoDelimiter() {
	return JavaElement.JEM_CLASSFILE;
}
/*
 * @see IJavaElement
 */
public IPath getPath() {
	return getParent().getPath().append(name);
}
/*
 * @see IJavaElement
 */
public IResource getResource() {
	PackageFragmentRoot root = this.getPackageFragmentRoot();
	if (root.isArchive()) {
		return root.getResource();
	} else {
		return ((IContainer)this.getParent().getResource()).getFile(new Path(this.getElementName()));
	}
}
/**
 * @see ISourceReference
 */
public String getSource() throws JavaModelException {
	IBuffer buffer = getBuffer();
	if (buffer == null) {
		return null;
	}
	return buffer.getContents();
}
/**
 * @see ISourceReference
 */
public ISourceRange getSourceRange() throws JavaModelException {
	IBuffer buffer = getBuffer();
	if (buffer != null) {
		String contents = buffer.getContents();
		if (contents == null) return null;
		return new SourceRange(0, contents.length());
	} else {
		return null;
	}
}
/*
 * Returns the name of the toplevel type of this class file.
 */
public String getTopLevelTypeName() {
    String topLevelTypeName = getElementName();
    int firstDollar = topLevelTypeName.indexOf('$');
    if (firstDollar != -1) {
        topLevelTypeName = topLevelTypeName.substring(0, firstDollar);
    } else {
        topLevelTypeName = topLevelTypeName.substring(0, topLevelTypeName.length()-SUFFIX_CLASS.length);
    }
    return topLevelTypeName;
}
/**
 * @see IClassFile
 */
/*
public IType getType() {
	if (this.binaryType == null) {
		this.binaryType = new BinaryType(this, getTypeName());
	}
	return this.binaryType;
}
*/
public String getTypeName() {
	// Internal class file name doesn't contain ".class" file extension
	int lastDollar = this.name.lastIndexOf('$');
	return lastDollar > -1 ? Util.localTypeName(this.name, lastDollar, this.name.length()) : this.name;
}
/*
 * @see IClassFile
 */
public ICompilationUnit getWorkingCopy(WorkingCopyOwner owner, IProgressMonitor monitor) throws JavaModelException {
	CompilationUnit workingCopy = new ClassFileWorkingCopy(this, owner == null ? DefaultWorkingCopyOwner.PRIMARY : owner);
	JavaModelManager manager = JavaModelManager.getJavaModelManager();
	JavaModelManager.PerWorkingCopyInfo perWorkingCopyInfo = 
		manager.getPerWorkingCopyInfo(workingCopy, false/*don't create*/, true/*record usage*/, null/*not used since don't create*/);
	if (perWorkingCopyInfo != null) {
		return perWorkingCopyInfo.getWorkingCopy(); // return existing handle instead of the one created above
	}
	BecomeWorkingCopyOperation op = new BecomeWorkingCopyOperation(workingCopy, null);
	op.runOperation(monitor);
	return workingCopy;
}
/**
 * @see IClassFile
 * @deprecated
 */
public IJavaElement getWorkingCopy(IProgressMonitor monitor, descent.core.IBufferFactory factory) throws JavaModelException {
	return getWorkingCopy(BufferFactoryWrapper.create(factory), monitor);
}
/**
 * @see Openable
 */
protected boolean hasBuffer() {
	return true;
}
public int hashCode() {
	return Util.combineHashCodes(this.name.hashCode(), this.parent.hashCode());
}
/**
 * @see IClassFile
 */
/*
public boolean isClass() throws JavaModelException {
	return getType().isClass();
}
*/
/**
 * @see IClassFile
 */
/*
public boolean isInterface() throws JavaModelException {
	return getType().isInterface();
}
*/
/**
 * Returns true - class files are always read only.
 */
public boolean isReadOnly() {
	return true;
}
/**
 * Opens and returns buffer on the source code associated with this class file.
 * Maps the source code to the children elements of this class file.
 * If no source code is associated with this class file, 
 * <code>null</code> is returned.
 * 
 * @see Openable
 */
/*
protected IBuffer openBuffer(IProgressMonitor pm, Object info) throws JavaModelException {
	SourceMapper mapper = getSourceMapper();
	if (mapper != null) {
		return mapSource(mapper, info instanceof IBinaryType ? (IBinaryType) info : null);
	}
	return null;
}
*/
/*
private IBuffer mapSource(SourceMapper mapper, IBinaryType info) {
	char[] contents = mapper.findSource(getType(), info);
	if (contents != null) {
		// create buffer
		IBuffer buffer = getBufferManager().createBuffer(this);
		if (buffer == null) return null;
		BufferManager bufManager = getBufferManager();
		bufManager.addBuffer(buffer);
		
		// set the buffer source
		if (buffer.getCharacters() == null){
			buffer.setContents(contents);
		}
		
		// listen to buffer changes
		buffer.addBufferChangedListener(this);	
				
		// do the source mapping
		mapper.mapSource(getType(), contents, info);
		
		return buffer;
	}
	return null;
}
*/
/* package */ static String simpleName(char[] className) {
	if (className == null)
		return null;
	String simpleName = new String(unqualifiedName(className));
	int lastDollar = simpleName.lastIndexOf('$');
	if (lastDollar != -1) 
		return Util.localTypeName(simpleName, lastDollar, simpleName.length());
	else
		return simpleName;
}
/**
 * Returns the Java Model representation of the given name
 * which is provided in diet class file format, or <code>null</code>
 * if the given name is <code>null</code>.
 *
 * <p><code>ClassFileReader</code> format is similar to "java/lang/Object",
 * and corresponding Java Model format is "java.lang.Object".
 */

public static char[] translatedName(char[] name) {
	if (name == null)
		return null;
	int nameLength = name.length;
	char[] newName= new char[nameLength];
	for (int i= 0; i < nameLength; i++) {
		if (name[i] == '/') {
			newName[i]= '.';
		} else {
			newName[i]= name[i];
		}
	}
	return newName;
}
/**
 * Returns the Java Model representation of the given names
 * which are provided in diet class file format, or <code>null</code>
 * if the given names are <code>null</code>.
 *
 * <p><code>ClassFileReader</code> format is similar to "java/lang/Object",
 * and corresponding Java Model format is "java.lang.Object".
 */

/* package */ static char[][] translatedNames(char[][] names) {
	if (names == null)
		return null;
	int length = names.length;
	char[][] newNames = new char[length][];
	for(int i = 0; i < length; i++) {
		newNames[i] = translatedName(names[i]);
	}
	return newNames;
}
/**
 * Returns the Java Model format of the unqualified class name for the
 * given className which is provided in diet class file format,
 * or <code>null</code> if the given className is <code>null</code>.
 * (This removes the package name, but not enclosing type names).
 *
 * <p><code>ClassFileReader</code> format is similar to "java/lang/Object",
 * and corresponding Java Model simple name format is "Object".
 */

/* package */ static char[] unqualifiedName(char[] className) {
	if (className == null)
		return null;
	int count = 0;
	for (int i = className.length - 1; i > -1; i--) {
		if (className[i] == '/') {
			char[] name = new char[count];
			System.arraycopy(className, i + 1, name, 0, count);
			return name;
		}
		count++;
	}
	return className;
}

/**
 * @see ICodeAssist#codeComplete(int, ICodeCompletionRequestor)
 * @deprecated - should use codeComplete(int, ICompletionRequestor) instead
 */
/*
public void codeComplete(int offset, final descent.core.ICodeCompletionRequestor requestor) throws JavaModelException {
	
	if (requestor == null){
		codeComplete(offset, (ICompletionRequestor)null);
		return;
	}
	codeComplete(
		offset,
		new ICompletionRequestor(){
			public void acceptAnonymousType(char[] superTypePackageName,char[] superTypeName, char[][] parameterPackageNames,char[][] parameterTypeNames,char[][] parameterNames,char[] completionName,int modifiers,int completionStart,int completionEnd, int relevance) {
				// ignore
			}
			public void acceptClass(char[] packageName, char[] className, char[] completionName, int modifiers, int completionStart, int completionEnd, int relevance) {
				requestor.acceptClass(packageName, className, completionName, modifiers, completionStart, completionEnd);
			}
			public void acceptError(IProblem error) {
				// was disabled in 1.0
			}
			public void acceptField(char[] declaringTypePackageName, char[] declaringTypeName, char[] fieldName, char[] typePackageName, char[] typeName, char[] completionName, int modifiers, int completionStart, int completionEnd, int relevance) {
				requestor.acceptField(declaringTypePackageName, declaringTypeName, fieldName, typePackageName, typeName, completionName, modifiers, completionStart, completionEnd);
			}
			public void acceptInterface(char[] packageName,char[] interfaceName,char[] completionName,int modifiers,int completionStart,int completionEnd, int relevance) {
				requestor.acceptInterface(packageName, interfaceName, completionName, modifiers, completionStart, completionEnd);
			}
			public void acceptKeyword(char[] keywordName,int completionStart,int completionEnd, int relevance){
				requestor.acceptKeyword(keywordName, completionStart, completionEnd);
			}
			public void acceptLabel(char[] labelName,int completionStart,int completionEnd, int relevance){
				requestor.acceptLabel(labelName, completionStart, completionEnd);
			}
			public void acceptLocalVariable(char[] localVarName,char[] typePackageName,char[] typeName,int modifiers,int completionStart,int completionEnd, int relevance){
				// ignore
			}
			public void acceptMethod(char[] declaringTypePackageName,char[] declaringTypeName,char[] selector,char[][] parameterPackageNames,char[][] parameterTypeNames,char[][] parameterNames,char[] returnTypePackageName,char[] returnTypeName,char[] completionName,int modifiers,int completionStart,int completionEnd, int relevance){
				// skip parameter names
				requestor.acceptMethod(declaringTypePackageName, declaringTypeName, selector, parameterPackageNames, parameterTypeNames, returnTypePackageName, returnTypeName, completionName, modifiers, completionStart, completionEnd);
			}
			public void acceptMethodDeclaration(char[] declaringTypePackageName,char[] declaringTypeName,char[] selector,char[][] parameterPackageNames,char[][] parameterTypeNames,char[][] parameterNames,char[] returnTypePackageName,char[] returnTypeName,char[] completionName,int modifiers,int completionStart,int completionEnd, int relevance){
				// ignore
			}
			public void acceptModifier(char[] modifierName,int completionStart,int completionEnd, int relevance){
				requestor.acceptModifier(modifierName, completionStart, completionEnd);
			}
			public void acceptPackage(char[] packageName,char[] completionName,int completionStart,int completionEnd, int relevance){
				requestor.acceptPackage(packageName, completionName, completionStart, completionEnd);
			}
			public void acceptType(char[] packageName,char[] typeName,char[] completionName,int completionStart,int completionEnd, int relevance){
				requestor.acceptType(packageName, typeName, completionName, completionStart, completionEnd);
			}
			public void acceptVariableName(char[] typePackageName,char[] typeName,char[] varName,char[] completionName,int completionStart,int completionEnd, int relevance){
				// ignore
			}
		});
}
*/
}
