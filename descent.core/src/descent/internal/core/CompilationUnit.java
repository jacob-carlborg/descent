/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Alex Smirnoff (alexsmr@sympatico.ca) - part of the changes to support Java-like extension 
 *                                                            (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=71460)
 *******************************************************************************/
package descent.internal.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PerformanceStats;

import descent.core.CompletionRequestor;
import descent.core.Flags;
import descent.core.IBuffer;
import descent.core.IBufferFactory;
import descent.core.ICodeAssist;
import descent.core.ICompilationUnit;
import descent.core.IEvaluationResult;
import descent.core.IImportContainer;
import descent.core.IImportDeclaration;
import descent.core.IJavaElement;
import descent.core.IJavaModelStatusConstants;
import descent.core.IJavaProject;
import descent.core.IMember;
import descent.core.IMethod;
import descent.core.IOpenable;
import descent.core.IPackageDeclaration;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.IProblemRequestor;
import descent.core.ISourceManipulation;
import descent.core.ISourceRange;
import descent.core.ISourceReference;
import descent.core.IType;
import descent.core.IWorkingCopy;
import descent.core.JavaConventions;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.core.dom.AST;
import descent.core.dom.ASTConverter;
import descent.core.dom.CompilationUnitResolver;
import descent.internal.compiler.SourceElementParser;
import descent.internal.compiler.impl.CompilerOptions;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.util.SuffixConstants;
import descent.internal.core.util.MementoTokenizer;
import descent.internal.core.util.Messages;
import descent.internal.core.util.Util;

/**
 * @see ICompilationUnit
 */
public class CompilationUnit extends Openable implements ICompilationUnit, descent.internal.compiler.env.ICompilationUnit, SuffixConstants {
	
	private static final IImportDeclaration[] NO_IMPORTS = new IImportDeclaration[0];
	
	protected String name;
	public WorkingCopyOwner owner;

/**
 * Constructs a handle to a compilation unit with the given name in the
 * specified package for the specified owner
 */
public CompilationUnit(PackageFragment parent, String name, WorkingCopyOwner owner) {
	super(parent);
	this.name = name;
	this.owner = owner;
}
/*
 * @see ICompilationUnit#becomeWorkingCopy(IProblemRequestor, IProgressMonitor)
 */
public void becomeWorkingCopy(IProblemRequestor problemRequestor, IProgressMonitor monitor) throws JavaModelException {
	JavaModelManager manager = JavaModelManager.getJavaModelManager();
	JavaModelManager.PerWorkingCopyInfo perWorkingCopyInfo = manager.getPerWorkingCopyInfo(this, false/*don't create*/, true /*record usage*/, null/*no problem requestor needed*/);
	if (perWorkingCopyInfo == null) {
		// close cu and its children
		close();

		BecomeWorkingCopyOperation operation = new BecomeWorkingCopyOperation(this, problemRequestor);
		operation.runOperation(monitor);
	}
}

protected boolean buildStructure(OpenableElementInfo info, final IProgressMonitor pm, Map newElements, IResource underlyingResource) throws JavaModelException {

	// check if this compilation unit can be opened
	if (!isWorkingCopy()) { // no check is done on root kind or exclusion pattern for working copies
		IStatus status = validateCompilationUnit(underlyingResource);
		if (!status.isOK()) throw newJavaModelException(status);
	}
	
	// prevents reopening of non-primary working copies (they are closed when they are discarded and should not be reopened)
	if (!isPrimary() && getPerWorkingCopyInfo() == null) {
		throw newNotPresentException();
	}

	CompilationUnitElementInfo unitInfo = (CompilationUnitElementInfo) info;

	// get buffer contents
	IBuffer buffer = getBufferManager().getBuffer(CompilationUnit.this);
	if (buffer == null) {
		buffer = openBuffer(pm, unitInfo); // open buffer independently from the info, since we are building the info
	}
	final char[] contents = buffer == null ? null : buffer.getCharacters();

	// generate structure and compute syntax problems if needed
	CompilationUnitStructureRequestor requestor = new CompilationUnitStructureRequestor(this, unitInfo, newElements);
	JavaModelManager.PerWorkingCopyInfo perWorkingCopyInfo = getPerWorkingCopyInfo();
	IJavaProject project = getJavaProject();

	boolean createAST;
	boolean resolveBindings;
	boolean statementsRecovery;
	HashMap problems;
	if (info instanceof ASTHolderCUInfo) {
		ASTHolderCUInfo astHolder = (ASTHolderCUInfo) info;
		createAST = astHolder.astLevel != NO_AST;
		resolveBindings = astHolder.resolveBindings;
		statementsRecovery = astHolder.statementsRecovery;
		problems = astHolder.problems;
	} else {
		createAST = false;
		resolveBindings = false;
		statementsRecovery = false;
		problems = null;
	}
	
	boolean computeProblems = perWorkingCopyInfo != null && perWorkingCopyInfo.isActive() && project != null && JavaProject.hasJavaNature(project.getProject());
	/*
	IProblemFactory problemFactory = new DefaultProblemFactory();
	*/
	Map options = project == null ? JavaCore.getOptions() : project.getOptions(true);
	if (!computeProblems) {
		// disable task tags checking to speed up parsing
		options.put(JavaCore.COMPILER_TASK_TAGS, ""); //$NON-NLS-1$
	}
	SourceElementParser parser = new SourceElementParser(requestor, new CompilerOptions(options));
	parser.diet = !computeProblems && !createAST;
	// parser.reportOnlyOneSyntaxError = !computeProblems;
	// parser.setStatementsRecovery(statementsRecovery);
	
	// if (!computeProblems && !resolveBindings && !createAST) // disable javadoc parsing if not computing problems, not resolving and not creating ast
	// 	parser.javadocParser.checkDocComment = false;
	requestor.source = contents;
	//requestor.parser = parser;
	
	Module module = parser.parseCompilationUnit(this);
	
	// update timestamp (might be IResource.NULL_STAMP if original does not exist)
	if (underlyingResource == null) {
		underlyingResource = getResource();
	}
	// underlying resource is null in the case of a working copy on a class file in a jar
	if (underlyingResource != null)
		unitInfo.timestamp = ((IFile)underlyingResource).getModificationStamp();
	
	// compute other problems if needed
	// CompilationUnit compilationUnitDeclaration = null;
	
	SemanticContext context = null;
	
	try {
		if (computeProblems) {
			
			context = CompilationUnitResolver.resolve(module, this.getJavaProject(), this.owner);
			
			if (problems == null) {
				// report problems to the problem requestor
				problems = new HashMap();
				/* TODO JDT problems
				compilationUnitDeclaration = CompilationUnitProblemFinder.process(unit, this, contents, parser, this.owner, problems, createAST, true, pm);
				*/
				problems.put(new Object(), module.problems.toArray(new IProblem[module.problems.size()]));
				try {
					perWorkingCopyInfo.beginReporting();
					for (Iterator iteraror = problems.values().iterator(); iteraror.hasNext();) {
						IProblem[] categorizedProblems = (IProblem[]) iteraror.next();
						if (categorizedProblems == null) continue;
						for (int i = 0, length = categorizedProblems.length; i < length; i++) {
							perWorkingCopyInfo.acceptProblem(categorizedProblems[i]);
						}
					}
				} finally {
					perWorkingCopyInfo.endReporting();
				}
			} else {
				// collect problems
				/* TODO JDT problems
				compilationUnitDeclaration = CompilationUnitProblemFinder.process(unit, this, contents, parser, this.owner, problems, createAST, true, pm);
				*/
				problems.put(new Object(), module.problems.toArray(new IProblem[module.problems.size()]));
			}
		}
		
		if (createAST) {
			// int astLevel = ((ASTHolderCUInfo) info).astLevel;
			// descent.core.dom.CompilationUnit cu = AST.convertCompilationUnit(astLevel, unit, contents, options, computeProblems, this, pm);
			// ((ASTHolderCUInfo) info).ast = cu;
			// TODO check if need to resolve bindings here
			ASTConverter converter = new ASTConverter(true, pm);
			converter.setAST(AST.newAST(AST.D2));
			converter.init(getJavaProject(), context, getOwner());
			((ASTHolderCUInfo) info).ast = converter.convert(module, this);
		}
	} finally {
		/*
	    if (compilationUnitDeclaration != null) {
	        compilationUnitDeclaration.cleanUp();
	    }
	    */
	}
	
	return unitInfo.isStructureKnown();
}
/*
 * @see Openable#canBeRemovedFromCache
 */
public boolean canBeRemovedFromCache() {
	if (getPerWorkingCopyInfo() != null) return false; // working copies should remain in the cache until they are destroyed
	return super.canBeRemovedFromCache();
}
/*
 * @see Openable#canBufferBeRemovedFromCache
 */
public boolean canBufferBeRemovedFromCache(IBuffer buffer) {
	if (getPerWorkingCopyInfo() != null) return false; // working copy buffers should remain in the cache until working copy is destroyed
	return super.canBufferBeRemovedFromCache(buffer);
}/*
 * @see IOpenable#close
 */
public void close() throws JavaModelException {
	if (getPerWorkingCopyInfo() != null) return; // a working copy must remain opened until it is discarded
	super.close();
}
/*
 * @see Openable#closing
 */
protected void closing(Object info) {
	if (getPerWorkingCopyInfo() == null) {
		super.closing(info);
	} // else the buffer of a working copy must remain open for the lifetime of the working copy
}

/* (non-Javadoc)
 * @see descent.core.ICodeAssist#codeComplete(int, descent.core.CompletionRequestor)
 */
public void codeComplete(int offset, CompletionRequestor requestor) throws JavaModelException {
	codeComplete(offset, requestor, DefaultWorkingCopyOwner.PRIMARY);
}

/* (non-Javadoc)
 * @see descent.core.ICodeAssist#codeComplete(int, descent.core.CompletionRequestor, descent.core.WorkingCopyOwner)
 */
public void codeComplete(int offset, CompletionRequestor requestor, WorkingCopyOwner workingCopyOwner) throws JavaModelException {
	codeComplete(this, isWorkingCopy() ? (descent.internal.compiler.env.ICompilationUnit) getOriginalElement() : this, offset, requestor, workingCopyOwner);
}

/**
 * @see ICodeAssist#codeSelect(int, int)
 */
public IJavaElement[] codeSelect(int offset, int length) throws JavaModelException {
	return codeSelect(offset, length, DefaultWorkingCopyOwner.PRIMARY);
}
/**
 * @see ICodeAssist#codeSelect(int, int, WorkingCopyOwner)
 */
public IJavaElement[] codeSelect(int offset, int length, WorkingCopyOwner workingCopyOwner) throws JavaModelException {
	return super.codeSelect(this, offset, length, workingCopyOwner);
}
public IEvaluationResult codeEvaluate(int offset) throws JavaModelException {
	return codeEvaluate(offset, DefaultWorkingCopyOwner.PRIMARY);
}
public IEvaluationResult codeEvaluate(int offset, WorkingCopyOwner owner) throws JavaModelException {
	return super.codeEvaluate(this, offset, owner);
}
/**
 * @see IWorkingCopy#commit(boolean, IProgressMonitor)
 * @deprecated
 */
public void commit(boolean force, IProgressMonitor monitor) throws JavaModelException {
	commitWorkingCopy(force, monitor);
}
/**
 * @see ICompilationUnit#commitWorkingCopy(boolean, IProgressMonitor)
 */
public void commitWorkingCopy(boolean force, IProgressMonitor monitor) throws JavaModelException {
	CommitWorkingCopyOperation op= new CommitWorkingCopyOperation(this, force);
	op.runOperation(monitor);
}
/**
 * @see ISourceManipulation#copy(IJavaElement, IJavaElement, String, boolean, IProgressMonitor)
 */
public void copy(IJavaElement container, IJavaElement sibling, String rename, boolean force, IProgressMonitor monitor) throws JavaModelException {
	if (container == null) {
		throw new IllegalArgumentException(Messages.operation_nullContainer); 
	}
	IJavaElement[] elements = new IJavaElement[] {this};
	IJavaElement[] containers = new IJavaElement[] {container};
	String[] renamings = null;
	if (rename != null) {
		renamings = new String[] {rename};
	}
	getJavaModel().copy(elements, containers, null, renamings, force, monitor);
}
/**
 * Returns a new element info for this element.
 */
protected Object createElementInfo() {
	return new CompilationUnitElementInfo();
}
/**
 * @see ICompilationUnit#createImport(String, IJavaElement, IProgressMonitor)
 */
public IImportDeclaration createImport(String importName, IJavaElement sibling, IProgressMonitor monitor) throws JavaModelException {
	return createImport(importName, sibling, Flags.AccDefault, monitor);
}

/**
 * @see ICompilationUnit#createImport(String, IJavaElement, int, IProgressMonitor)
 * @since 3.0
 */
public IImportDeclaration createImport(String importName, IJavaElement sibling, int flags, IProgressMonitor monitor) throws JavaModelException {
	CreateImportOperation op = new CreateImportOperation(importName, this, flags);
	if (sibling != null) {
		op.createBefore(sibling);
	}
	op.runOperation(monitor);
	return getImport(importName);
}

/**
 * @see ICompilationUnit#createPackageDeclaration(String, IProgressMonitor)
 */
public IPackageDeclaration createPackageDeclaration(String pkg, IProgressMonitor monitor) throws JavaModelException {
	CreatePackageDeclarationOperation op= new CreatePackageDeclarationOperation(pkg, this);
	op.runOperation(monitor);
	return getPackageDeclaration(pkg);
}
/**
 * @see ICompilationUnit#createType(String, IJavaElement, boolean, IProgressMonitor)
 */
public IType createType(String content, IJavaElement sibling, boolean force, IProgressMonitor monitor) throws JavaModelException {
	if (!exists()) {
		//autogenerate this compilation unit
		IPackageFragment pkg = (IPackageFragment) getParent();
		String source = ""; //$NON-NLS-1$
		if (!pkg.isDefaultPackage()) {
			//not the default package...add the package declaration
			String lineSeparator = Util.getLineSeparator(null, getJavaProject());
			source = "package " + pkg.getElementName() + ";"  + lineSeparator + lineSeparator; //$NON-NLS-1$ //$NON-NLS-2$
		}
		CreateCompilationUnitOperation op = new CreateCompilationUnitOperation(pkg, this.name, source, force);
		op.runOperation(monitor);
	}
	CreateTypeOperation op = new CreateTypeOperation(this, content, force);
	if (sibling != null) {
		op.createBefore(sibling);
	}
	op.runOperation(monitor);
	return (IType) op.getResultElements()[0];
}
/**
 * @see ISourceManipulation#delete(boolean, IProgressMonitor)
 */
public void delete(boolean force, IProgressMonitor monitor) throws JavaModelException {
	IJavaElement[] elements= new IJavaElement[] {this};
	getJavaModel().delete(elements, force, monitor);
}
/**
 * @see IWorkingCopy#destroy()
 * @deprecated
 */
public void destroy() {
	try {
		discardWorkingCopy();
	} catch (JavaModelException e) {
		if (JavaModelManager.VERBOSE)
			e.printStackTrace();
	}
}
/*
 * @see ICompilationUnit#discardWorkingCopy
 */
public void discardWorkingCopy() throws JavaModelException {
	// discard working copy and its children
	DiscardWorkingCopyOperation op = new DiscardWorkingCopyOperation(this);
	op.runOperation(null);
}
/**
 * Returns true if this handle represents the same Java element
 * as the given handle.
 *
 * @see Object#equals(java.lang.Object)
 */
public boolean equals(Object obj) {
	if (!(obj instanceof CompilationUnit)) return false;
	CompilationUnit other = (CompilationUnit)obj;
	return this.owner.equals(other.owner) && super.equals(obj);
}
public boolean exists() {
	// working copy always exists in the model until it is gotten rid of (even if not on classpath)
	if (getPerWorkingCopyInfo() != null) return true;	
	
	// if not a working copy, it exists only if it is a primary compilation unit
	return isPrimary() && validateCompilationUnit(getResource()).isOK();
}
/**
 * @see ICompilationUnit#findElements(IJavaElement)
 */
public IJavaElement[] findElements(IJavaElement element) {
	ArrayList children = new ArrayList();
	while (element != null && element.getElementType() != IJavaElement.COMPILATION_UNIT) {
		children.add(element);
		element = element.getParent();
	}
	if (element == null) return null;
	IJavaElement currentElement = this;
	for (int i = children.size()-1; i >= 0; i--) {
		SourceRefElement child = (SourceRefElement)children.get(i);
		switch (child.getElementType()) {
			case IJavaElement.PACKAGE_DECLARATION:
				currentElement = ((ICompilationUnit)currentElement).getPackageDeclaration(child.getElementName());
				break;
			case IJavaElement.IMPORT_CONTAINER:
				currentElement = ((ICompilationUnit)currentElement).getImportContainer();
				break;
			case IJavaElement.IMPORT_DECLARATION:
				currentElement = ((IImportContainer)currentElement).getImport(child.getElementName());
				break;
			case IJavaElement.TYPE:
				switch (currentElement.getElementType()) {
					case IJavaElement.COMPILATION_UNIT:
						currentElement = ((ICompilationUnit)currentElement).getType(child.getElementName());
						break;
					case IJavaElement.TYPE:
						currentElement = ((IType)currentElement).getType(child.getElementName());
						break;
					case IJavaElement.FIELD:
					case IJavaElement.INITIALIZER:
					case IJavaElement.METHOD:
						currentElement =  ((IMember)currentElement).getType(child.getElementName(), child.occurrenceCount);
						break;
				}
				break;
			case IJavaElement.INITIALIZER:
				currentElement = ((IType)currentElement).getInitializer(child.occurrenceCount);
				break;
			case IJavaElement.FIELD:
				currentElement = ((IType)currentElement).getField(child.getElementName());
				break;
			case IJavaElement.METHOD:
				currentElement = ((IType)currentElement).getMethod(child.getElementName(), ((IMethod)child).getParameterTypes());
				break;
		}
		
	}
	if (currentElement != null && currentElement.exists()) {
		return new IJavaElement[] {currentElement};
	} else {
		return null;
	}
}
/**
 * @see ICompilationUnit#findPrimaryType()
 */
public IType findPrimaryType() {
	String typeName = Util.getNameWithoutJavaLikeExtension(getElementName());
	IType primaryType= getType(typeName);
	if (primaryType.exists()) {
		return primaryType;
	}
	return null;
}

/**
 * @see IWorkingCopy#findSharedWorkingCopy(IBufferFactory)
 * @deprecated
 */
public IJavaElement findSharedWorkingCopy(IBufferFactory factory) {

	// if factory is null, default factory must be used
	if (factory == null) factory = this.getBufferManager().getDefaultBufferFactory();
	
	return findWorkingCopy(BufferFactoryWrapper.create(factory));
}

/**
 * @see ICompilationUnit#findWorkingCopy(WorkingCopyOwner)
 */
public ICompilationUnit findWorkingCopy(WorkingCopyOwner workingCopyOwner) {
	CompilationUnit cu = new CompilationUnit((PackageFragment)this.parent, getElementName(), workingCopyOwner);
	if (workingCopyOwner == DefaultWorkingCopyOwner.PRIMARY) {
		return cu;
	} else {
		// must be a working copy
		JavaModelManager.PerWorkingCopyInfo perWorkingCopyInfo = cu.getPerWorkingCopyInfo();
		if (perWorkingCopyInfo != null) {
			return perWorkingCopyInfo.getWorkingCopy();
		} else {
			return null;
		}
	}
}
/**
 * @see ICompilationUnit#getAllTypes()
 */
public IType[] getAllTypes() throws JavaModelException {
	IJavaElement[] types = getTypes();
	int i;
	ArrayList allTypes = new ArrayList(types.length);
	ArrayList typesToTraverse = new ArrayList(types.length);
	for (i = 0; i < types.length; i++) {
		typesToTraverse.add(types[i]);
	}
	while (!typesToTraverse.isEmpty()) {
		IType type = (IType) typesToTraverse.get(0);
		typesToTraverse.remove(type);
		allTypes.add(type);
		types = type.getTypes();
		for (i = 0; i < types.length; i++) {
			typesToTraverse.add(types[i]);
		}
	} 
	IType[] arrayOfAllTypes = new IType[allTypes.size()];
	allTypes.toArray(arrayOfAllTypes);
	return arrayOfAllTypes;
}
/**
 * @see IMember#getCompilationUnit()
 */
public ICompilationUnit getCompilationUnit() {
	return this;
}
/**
 * @see descent.internal.compiler.env.ICompilationUnit#getContents()
 */
public char[] getContents() {
	IBuffer buffer = getBufferManager().getBuffer(this);
	if (buffer == null) {
		// no need to force opening of CU to get the content
		// also this cannot be a working copy, as its buffer is never closed while the working copy is alive
		try {
			return Util.getResourceContentsAsCharArray((IFile) getResource());
		} catch (JavaModelException e) {
			return CharOperation.NO_CHAR;
		}
	}
	char[] contents = buffer.getCharacters();
	if (contents == null) // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=129814
		return CharOperation.NO_CHAR;
	return contents;
}
/**
 * A compilation unit has a corresponding resource unless it is contained
 * in a jar.
 *
 * @see IJavaElement#getCorrespondingResource()
 */
public IResource getCorrespondingResource() throws JavaModelException {
	PackageFragmentRoot root = getPackageFragmentRoot();
	if (root == null || root.isArchive()) {
		return null;
	} else {
		return getUnderlyingResource();
	}
}
/**
 * @see ICompilationUnit#getElementAt(int)
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
	return COMPILATION_UNIT;
}
/**
 * @see descent.internal.compiler.env.IDependent#getFileName()
 */
public char[] getFileName() {
	IPackageFragmentRoot root = (IPackageFragmentRoot) getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
	return getPath().removeFirstSegments(root.getPath().segmentCount()).toString().toCharArray();
}

/*
 * @see JavaElement
 */
public IJavaElement getHandleFromMemento(String token, MementoTokenizer memento, WorkingCopyOwner workingCopyOwner) {
	switch (token.charAt(0)) {
		case JEM_IMPORTDECLARATION:
			JavaElement container = (JavaElement)getImportContainer();
			return container.getHandleFromMemento(token, memento, workingCopyOwner);
		case JEM_PACKAGEDECLARATION:
			if (!memento.hasMoreTokens()) return this;
			String pkgName = memento.nextToken();
			JavaElement pkgDecl = (JavaElement)getPackageDeclaration(pkgName);
			return pkgDecl.getHandleFromMemento(memento, workingCopyOwner);
		case JEM_TYPE:
			if (!memento.hasMoreTokens()) return this;
			String typeName = memento.nextToken();
			JavaElement type = (JavaElement)getType(typeName);
			return type.getHandleFromMemento(memento, workingCopyOwner);
	}
	return null;
}

/**
 * @see JavaElement#getHandleMementoDelimiter()
 */
protected char getHandleMementoDelimiter() {
	return JavaElement.JEM_COMPILATIONUNIT;
}
/**
 * @see ICompilationUnit#getImport(String)
 */
public IImportDeclaration getImport(String importName) {
	return getImportContainer().getImport(importName);
}
/**
 * @see ICompilationUnit#getImportContainer()
 */
public IImportContainer getImportContainer() {
	return new ImportContainer(this);
}


/**
 * @see ICompilationUnit#getImports()
 */
public IImportDeclaration[] getImports() throws JavaModelException {
	IImportContainer container= getImportContainer();
	JavaModelManager manager = JavaModelManager.getJavaModelManager();
	Object info = manager.getInfo(container);
	if (info == null) {
		if (manager.getInfo(this) != null)
			// CU was opened, but no import container, then no imports
			return NO_IMPORTS;
		else {
			open(null); // force opening of CU
			info = manager.getInfo(container);
			if (info == null) 
				// after opening, if no import container, then no imports
				return NO_IMPORTS;
		}	
	}
	IJavaElement[] elements = ((JavaElementInfo) info).children;
	int length = elements.length;
	IImportDeclaration[] imports = new IImportDeclaration[length];
	System.arraycopy(elements, 0, imports, 0, length);
	return imports;
}
/**
 * @see descent.internal.compiler.env.ICompilationUnit#getMainTypeName()
 */
public char[] getMainTypeName(){
	return Util.getNameWithoutJavaLikeExtension(getElementName()).toCharArray();
}
/**
 * @see IWorkingCopy#getOriginal(IJavaElement)
 * @deprecated
 */
public IJavaElement getOriginal(IJavaElement workingCopyElement) {
	// backward compatibility
	if (!isWorkingCopy()) return null;
	CompilationUnit cu = (CompilationUnit)workingCopyElement.getAncestor(COMPILATION_UNIT);
	if (cu == null || !this.owner.equals(cu.owner)) {
		return null;
	}
	
	return workingCopyElement.getPrimaryElement();
}
/**
 * @see IWorkingCopy#getOriginalElement()
 * @deprecated
 */
public IJavaElement getOriginalElement() {
	// backward compatibility
	if (!isWorkingCopy()) return null;
	
	return getPrimaryElement();
}
/*
 * @see ICompilationUnit#getOwner()
 */
public WorkingCopyOwner getOwner() {
	return isPrimary() || !isWorkingCopy() ? null : this.owner;
}
/**
 * @see ICompilationUnit#getPackageDeclaration(String)
 */
public IPackageDeclaration getPackageDeclaration(String pkg) {
	return new PackageDeclaration(this, pkg);
}
/**
 * @see ICompilationUnit#getPackageDeclarations()
 */
public IPackageDeclaration[] getPackageDeclarations() throws JavaModelException {
	ArrayList list = getChildrenOfType(PACKAGE_DECLARATION);
	IPackageDeclaration[] array= new IPackageDeclaration[list.size()];
	list.toArray(array);
	return array;
}
/**
 * @see descent.internal.compiler.env.ICompilationUnit#getPackageName()
 */
public char[][] getPackageName() {
	PackageFragment packageFragment = (PackageFragment) getParent();
	if (packageFragment == null) return CharOperation.NO_CHAR_CHAR;
	return Util.toCharArrays(packageFragment.names);
}

/**
 * @see IJavaElement#getPath()
 */
public IPath getPath() {
	PackageFragmentRoot root = getPackageFragmentRoot();
	if (root == null) return new Path(getElementName()); // working copy not in workspace
	if (root.isArchive()) {
		return root.getPath();
	} else {
		return getParent().getPath().append(getElementName());
	}
}
/*
 * Returns the per working copy info for the receiver, or null if none exist.
 * Note: the use count of the per working copy info is NOT incremented.
 */
public JavaModelManager.PerWorkingCopyInfo getPerWorkingCopyInfo() {
	return JavaModelManager.getJavaModelManager().getPerWorkingCopyInfo(this, false/*don't create*/, false/*don't record usage*/, null/*no problem requestor needed*/);
}
/*
 * @see ICompilationUnit#getPrimary()
 */
public ICompilationUnit getPrimary() {
	return (ICompilationUnit)getPrimaryElement(true);
}
/*
 * @see JavaElement#getPrimaryElement(boolean)
 */
public IJavaElement getPrimaryElement(boolean checkOwner) {
	if (checkOwner && isPrimary()) return this;
	return new CompilationUnit((PackageFragment)getParent(), getElementName(), DefaultWorkingCopyOwner.PRIMARY);
}
/**
 * @see IJavaElement#getResource()
 */
public IResource getResource() {
	PackageFragmentRoot root = getPackageFragmentRoot();
	if (root == null) return null; // working copy not in workspace
	if (root.isArchive()) {
		return root.getResource();
	} else {
		return ((IContainer) getParent().getResource()).getFile(new Path(getElementName()));
	}
}
/**
 * @see ISourceReference#getSource()
 */
public String getSource() throws JavaModelException {
	IBuffer buffer = getBuffer();
	if (buffer == null) return ""; //$NON-NLS-1$
	return buffer.getContents();
}
/**
 * @see ISourceReference#getSourceRange()
 */
public ISourceRange getSourceRange() throws JavaModelException {
	return ((CompilationUnitElementInfo) getElementInfo()).getSourceRange();
}
/**
 * @see ICompilationUnit#getType(String)
 */
public IType getType(String typeName) {
	return new SourceType(this, typeName);
}
/**
 * @see ICompilationUnit#getTypes()
 */
public IType[] getTypes() throws JavaModelException {
	ArrayList list = getChildrenOfType(TYPE);
	IType[] array= new IType[list.size()];
	list.toArray(array);
	return array;
}
/**
 * @see IJavaElement
 */
public IResource getUnderlyingResource() throws JavaModelException {
	if (isWorkingCopy() && !isPrimary()) return null;
	return super.getUnderlyingResource();
}
/**
 * @see IWorkingCopy#getSharedWorkingCopy(IProgressMonitor, IBufferFactory, IProblemRequestor)
 * @deprecated
 */
public IJavaElement getSharedWorkingCopy(IProgressMonitor pm, IBufferFactory factory, IProblemRequestor problemRequestor) throws JavaModelException {
	
	// if factory is null, default factory must be used
	if (factory == null) factory = this.getBufferManager().getDefaultBufferFactory();
	
	return getWorkingCopy(BufferFactoryWrapper.create(factory), problemRequestor, pm);
}
/**
 * @see IWorkingCopy#getWorkingCopy()
 * @deprecated
 */
public IJavaElement getWorkingCopy() throws JavaModelException {
	return getWorkingCopy(null);
}
/**
 * @see ICompilationUnit#getWorkingCopy(IProgressMonitor)
 */
public ICompilationUnit getWorkingCopy(IProgressMonitor monitor) throws JavaModelException {
	return getWorkingCopy(new WorkingCopyOwner() {/*non shared working copy*/}, null/*no problem requestor*/, monitor);
}
/**
 * @see IWorkingCopy#getWorkingCopy(IProgressMonitor, IBufferFactory, IProblemRequestor)
 * @deprecated
 */
public IJavaElement getWorkingCopy(IProgressMonitor monitor, IBufferFactory factory, IProblemRequestor problemRequestor) throws JavaModelException {
	return getWorkingCopy(BufferFactoryWrapper.create(factory), problemRequestor, monitor);
}
/**
 * @see ICompilationUnit#getWorkingCopy(WorkingCopyOwner, IProblemRequestor, IProgressMonitor)
 */
public ICompilationUnit getWorkingCopy(WorkingCopyOwner workingCopyOwner, IProblemRequestor problemRequestor, IProgressMonitor monitor) throws JavaModelException {
	if (!isPrimary()) return this;
	
	JavaModelManager manager = JavaModelManager.getJavaModelManager();
	
	CompilationUnit workingCopy = new CompilationUnit((PackageFragment)getParent(), getElementName(), workingCopyOwner);
	JavaModelManager.PerWorkingCopyInfo perWorkingCopyInfo = 
		manager.getPerWorkingCopyInfo(workingCopy, false/*don't create*/, true/*record usage*/, null/*not used since don't create*/);
	if (perWorkingCopyInfo != null) {
		return perWorkingCopyInfo.getWorkingCopy(); // return existing handle instead of the one created above
	}
	BecomeWorkingCopyOperation op = new BecomeWorkingCopyOperation(workingCopy, problemRequestor);
	op.runOperation(monitor);
	return workingCopy;
}
/**
 * @see Openable#hasBuffer()
 */
protected boolean hasBuffer() {
	return true;
}
/*
 * @see ICompilationUnit#hasResourceChanged()
 */
public boolean hasResourceChanged() {
	if (!isWorkingCopy()) return false;
	
	// if resource got deleted, then #getModificationStamp() will answer IResource.NULL_STAMP, which is always different from the cached
	// timestamp
	Object info = JavaModelManager.getJavaModelManager().getInfo(this);
	if (info == null) return false;
	IResource resource = getResource();
	if (resource == null) return false;
	return ((CompilationUnitElementInfo)info).timestamp != resource.getModificationStamp();
}
/**
 * @see IWorkingCopy#isBasedOn(IResource)
 * @deprecated
 */
public boolean isBasedOn(IResource resource) {
	if (!isWorkingCopy()) return false;
	if (!getResource().equals(resource)) return false;
	return !hasResourceChanged();
}
/**
 * @see IOpenable#isConsistent()
 */
public boolean isConsistent() {
	return !JavaModelManager.getJavaModelManager().getElementsOutOfSynchWithBuffers().contains(this);
}
public boolean isPrimary() {
	return this.owner == DefaultWorkingCopyOwner.PRIMARY;
}
/**
 * @see Openable#isSourceElement()
 */
protected boolean isSourceElement() {
	return true;
}
protected IStatus validateCompilationUnit(IResource resource) {
	IPackageFragmentRoot root = getPackageFragmentRoot();
	// root never null as validation is not done for working copies
	try {
		if (root.getKind() != IPackageFragmentRoot.K_SOURCE) 
			return new JavaModelStatus(IJavaModelStatusConstants.INVALID_ELEMENT_TYPES, root);
	} catch (JavaModelException e) {
		return e.getJavaModelStatus();
	}
	if (resource != null) {
		char[][] inclusionPatterns = ((PackageFragmentRoot)root).fullInclusionPatternChars();
		char[][] exclusionPatterns = ((PackageFragmentRoot)root).fullExclusionPatternChars();
		if (Util.isExcluded(resource, inclusionPatterns, exclusionPatterns)) 
			return new JavaModelStatus(IJavaModelStatusConstants.ELEMENT_NOT_ON_CLASSPATH, this);
		if (!resource.isAccessible())
			return new JavaModelStatus(IJavaModelStatusConstants.ELEMENT_DOES_NOT_EXIST, this);
	}
	return JavaConventions.validateCompilationUnitName(getElementName());
}
/*
 * @see ICompilationUnit#isWorkingCopy()
 */
public boolean isWorkingCopy() {
	// For backward compatibility, non primary working copies are always returning true; in removal
	// delta, clients can still check that element was a working copy before being discarded.
	return !isPrimary() || getPerWorkingCopyInfo() != null;
}
/**
 * @see IOpenable#makeConsistent(IProgressMonitor)
 */
public void makeConsistent(IProgressMonitor monitor) throws JavaModelException {
	makeConsistent(NO_AST, false/*don't resolve bindings*/, false /* don't perform statements recovery */, null/*don't collect problems but report them*/, monitor);
}
public descent.core.dom.CompilationUnit makeConsistent(int astLevel, boolean resolveBindings, boolean statementsRecovery, HashMap problems, IProgressMonitor monitor) throws JavaModelException {
	if (isConsistent()) return null;
		
	// create a new info and make it the current info
	// (this will remove the info and its children just before storing the new infos)
	if (astLevel != NO_AST || problems != null) {
		ASTHolderCUInfo info = new ASTHolderCUInfo();
		info.astLevel = astLevel;
		info.resolveBindings = resolveBindings;
		info.statementsRecovery = statementsRecovery;
		info.problems = problems;
		openWhenClosed(info, monitor);
		descent.core.dom.CompilationUnit result = info.ast;
		info.ast = null;
		return result;
	} else {
		openWhenClosed(createElementInfo(), monitor);
		return null;
	}
}
/**
 * @see ISourceManipulation#move(IJavaElement, IJavaElement, String, boolean, IProgressMonitor)
 */
public void move(IJavaElement container, IJavaElement sibling, String rename, boolean force, IProgressMonitor monitor) throws JavaModelException {
	if (container == null) {
		throw new IllegalArgumentException(Messages.operation_nullContainer); 
	}
	IJavaElement[] elements= new IJavaElement[] {this};
	IJavaElement[] containers= new IJavaElement[] {container};
	
	String[] renamings= null;
	if (rename != null) {
		renamings= new String[] {rename};
	}
	getJavaModel().move(elements, containers, null, renamings, force, monitor);
}

/**
 * @see Openable#openBuffer(IProgressMonitor, Object)
 */
protected IBuffer openBuffer(IProgressMonitor pm, Object info) throws JavaModelException {

	// create buffer
	boolean isWorkingCopy = isWorkingCopy();
	IBuffer buffer = 
		isWorkingCopy 
			? this.owner.createBuffer(this) 
			: BufferManager.getDefaultBufferManager().createBuffer(this);
	if (buffer == null) return null;
	
	if (buffer.getOwner() == null) {
		buffer = 
			isWorkingCopy 
				? this.owner.createBuffer(this) 
				: BufferManager.getDefaultBufferManager().createBuffer(this);
	}
	
	// set the buffer source
	if (buffer.getCharacters() == null) {
		if (isWorkingCopy) {
			ICompilationUnit original;
			if (!isPrimary() 
					&& (original = new CompilationUnit((PackageFragment)getParent(), getElementName(), DefaultWorkingCopyOwner.PRIMARY)).isOpen()) {
				buffer.setContents(original.getSource());
			} else {
				IFile file = (IFile)getResource();
				if (file == null || !file.exists()) {
					// initialize buffer with empty contents
					buffer.setContents(CharOperation.NO_CHAR);
				} else {
					buffer.setContents(Util.getResourceContentsAsCharArray(file));
				}
			}
		} else {
			IFile file = (IFile)this.getResource();
			if (file == null || !file.exists()) throw newNotPresentException();
			buffer.setContents(Util.getResourceContentsAsCharArray(file));
		}
	}

	// add buffer to buffer cache
	BufferManager bufManager = getBufferManager();
	bufManager.addBuffer(buffer);
			
	// listen to buffer changes
	buffer.addBufferChangedListener(this);
	
	return buffer;
}
protected void openParent(Object childInfo, HashMap newElements, IProgressMonitor pm) throws JavaModelException {
	if (!isWorkingCopy())
		super.openParent(childInfo, newElements, pm);
	// don't open parent for a working copy to speed up the first becomeWorkingCopy
	// (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=89411)
}
/**
 * @see ICompilationUnit#reconcile()
 * @deprecated
 */
public IMarker[] reconcile() throws JavaModelException {
	reconcile(NO_AST, false/*don't force problem detection*/, false, null/*use primary owner*/, null/*no progress monitor*/);
	return null;
}
/**
 * @see ICompilationUnit#reconcile(int, boolean, WorkingCopyOwner, IProgressMonitor)
 */
public void reconcile(boolean forceProblemDetection, IProgressMonitor monitor) throws JavaModelException {
	reconcile(NO_AST, forceProblemDetection, false, null/*use primary owner*/, monitor);
}

/**
 * @see ICompilationUnit#reconcile(int, boolean, WorkingCopyOwner, IProgressMonitor)
 * @since 3.0
 */
public descent.core.dom.CompilationUnit reconcile(
	int astLevel,
	boolean forceProblemDetection,
	WorkingCopyOwner workingCopyOwner,
	IProgressMonitor monitor)
	throws JavaModelException {
	return reconcile(astLevel, forceProblemDetection, false, workingCopyOwner, monitor);
}
		
/**
 * @see ICompilationUnit#reconcile(int, boolean, WorkingCopyOwner, IProgressMonitor)
 * @since 3.0
 */
public descent.core.dom.CompilationUnit reconcile(
	int astLevel,
	boolean forceProblemDetection,
	boolean enableStatementsRecovery,
	WorkingCopyOwner workingCopyOwner,
	IProgressMonitor monitor)
	throws JavaModelException {
	
	if (!isWorkingCopy()) return null; // Reconciling is not supported on non working copies
	if (workingCopyOwner == null) workingCopyOwner = DefaultWorkingCopyOwner.PRIMARY;
	
	
	PerformanceStats stats = null;
	if(ReconcileWorkingCopyOperation.PERF) {
		stats = PerformanceStats.getStats(JavaModelManager.RECONCILE_PERF, this);
		stats.startRun(new String(this.getFileName()));
	}
	ReconcileWorkingCopyOperation op = new ReconcileWorkingCopyOperation(this, astLevel, forceProblemDetection, enableStatementsRecovery,workingCopyOwner);
	JavaModelManager manager = JavaModelManager.getJavaModelManager();
	try {
		manager.cacheZipFiles(); // cache zip files for performance (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=134172)
		op.runOperation(monitor);
	} finally {
		manager.flushZipFiles();
	}
	if(ReconcileWorkingCopyOperation.PERF) {
		stats.endRun();
	}
	return op.ast;
}

/**
 * @see ISourceManipulation#rename(String, boolean, IProgressMonitor)
 */
public void rename(String newName, boolean force, IProgressMonitor monitor) throws JavaModelException {
	if (newName == null) {
		throw new IllegalArgumentException(Messages.operation_nullName); 
	}
	IJavaElement[] elements= new IJavaElement[] {this};
	IJavaElement[] dests= new IJavaElement[] {this.getParent()};
	String[] renamings= new String[] {newName};
	getJavaModel().rename(elements, dests, renamings, force, monitor);
}
/*
 * @see ICompilationUnit
 */
public void restore() throws JavaModelException {

	if (!isWorkingCopy()) return;

	CompilationUnit original = (CompilationUnit) getOriginalElement();
	IBuffer buffer = this.getBuffer();
	if (buffer == null) return;
	buffer.setContents(original.getContents());
	updateTimeStamp(original);
	makeConsistent(null);
}
/**
 * @see IOpenable
 */
public void save(IProgressMonitor pm, boolean force) throws JavaModelException {
	if (isWorkingCopy()) {
		// no need to save the buffer for a working copy (this is a noop)
		reconcile();   // not simply makeConsistent, also computes fine-grain deltas
								// in case the working copy is being reconciled already (if not it would miss
								// one iteration of deltas).
	} else {		
		super.save(pm, force);
	}
}
public String getModuleName() {
	return this.name.substring(0, this.name.lastIndexOf('.'));
}
public String getFullyQualifiedName() {
	StringBuilder sb = new StringBuilder();
	sb.insert(0, getModuleName());
	
	JavaElement p = parent;
	loop:
		while(p != null) {
			switch(p.getElementType()) {
			case IJavaElement.PACKAGE_FRAGMENT:
				if (((IPackageFragment) p).isDefaultPackage()) {
					break loop;
				} else {
					if (sb.length() > 0) {
						sb.insert(0, '.');
					}
				}
				sb.insert(0, p.getElementName());
				break;
			default:
				break loop;
			}
			
			p = p.parent;
		}
	
	return sb.toString();
}
/**
 * Debugging purposes
 */
protected void toStringInfo(int tab, StringBuffer buffer, Object info, boolean showResolvedInfo) {
	if (!isPrimary()) {
		buffer.append(this.tabString(tab));
		buffer.append("[Working copy] "); //$NON-NLS-1$
		toStringName(buffer);
	} else {
		if (isWorkingCopy()) {
			buffer.append(this.tabString(tab));
			buffer.append("[Working copy] "); //$NON-NLS-1$
			toStringName(buffer);
			if (info == null) {
				buffer.append(" (not open)"); //$NON-NLS-1$
			}
		} else {
			super.toStringInfo(tab, buffer, info, showResolvedInfo);
		}
	}
}
/*
 * Assume that this is a working copy
 */
protected void updateTimeStamp(CompilationUnit original) throws JavaModelException {
	long timeStamp =
		((IFile) original.getResource()).getModificationStamp();
	if (timeStamp == IResource.NULL_STAMP) {
		throw new JavaModelException(
			new JavaModelStatus(IJavaModelStatusConstants.INVALID_RESOURCE));
	}
	((CompilationUnitElementInfo) getElementInfo()).timestamp = timeStamp;
}
@Override
protected void appendElementSignature(StringBuilder sb) throws JavaModelException {
	parent.appendElementSignature(sb);
	
	String moduleName = this.getModuleName();
	sb.append(moduleName.length());
	sb.append(moduleName);
}

}
