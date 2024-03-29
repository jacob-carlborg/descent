/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.core;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.PerformanceStats;

import descent.core.BufferChangedEvent;
import descent.core.CompletionRequestor;
import descent.core.IBuffer;
import descent.core.IBufferChangedListener;
import descent.core.IBufferFactory;
import descent.core.IEvaluationResult;
import descent.core.IJavaElement;
import descent.core.IJavaModelStatusConstants;
import descent.core.IOpenable;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.internal.codeassist.CompletionEngine;
import descent.internal.codeassist.EvaluationEngine;
import descent.internal.codeassist.SelectionEngine;
import descent.internal.core.util.Util;


/**
 * Abstract class for implementations of java elements which are IOpenable.
 *
 * @see IJavaElement
 * @see IOpenable
 */
public abstract class Openable extends JavaElement implements IOpenable, IBufferChangedListener {

protected Openable(JavaElement parent) {
	super(parent);
}
/**
 * The buffer associated with this element has changed. Registers
 * this element as being out of synch with its buffer's contents.
 * If the buffer has been closed, this element is set as NOT out of
 * synch with the contents.
 *
 * @see IBufferChangedListener
 */
public void bufferChanged(BufferChangedEvent event) {
	if (event.getBuffer().isClosed()) {
		JavaModelManager.getJavaModelManager().getElementsOutOfSynchWithBuffers().remove(this);
		getBufferManager().removeBuffer(event.getBuffer());
	} else {
		JavaModelManager.getJavaModelManager().getElementsOutOfSynchWithBuffers().add(this);
	}
}	
/**
 * Builds this element's structure and properties in the given
 * info object, based on this element's current contents (reuse buffer
 * contents if this element has an open buffer, or resource contents
 * if this element does not have an open buffer). Children
 * are placed in the given newElements table (note, this element
 * has already been placed in the newElements table). Returns true
 * if successful, or false if an error is encountered while determining
 * the structure of this element.
 */
protected abstract boolean buildStructure(OpenableElementInfo info, IProgressMonitor pm, Map newElements, IResource underlyingResource) throws JavaModelException;
/*
 * Returns whether this element can be removed from the Java model cache to make space.
 */
public boolean canBeRemovedFromCache() {
	try {
		return !hasUnsavedChanges();
	} catch (JavaModelException e) {
		return false;
	}
}
/*
 * Returns whether the buffer of this element can be removed from the Java model cache to make space.
 */
public boolean canBufferBeRemovedFromCache(IBuffer buffer) {
	return !buffer.hasUnsavedChanges();
}
/**
 * Close the buffer associated with this element, if any.
 */
protected void closeBuffer() {
	if (!hasBuffer()) return; // nothing to do
	IBuffer buffer = getBufferManager().getBuffer(this);
	if (buffer != null) {
		buffer.close();
		buffer.removeBufferChangedListener(this);
	}
}
/**
 * This element is being closed.  Do any necessary cleanup.
 */
protected void closing(Object info) {
	closeBuffer();
}

protected void codeComplete(descent.internal.compiler.env.ICompilationUnit cu, descent.internal.compiler.env.ICompilationUnit unitToSkip, int position, CompletionRequestor requestor, WorkingCopyOwner owner) throws JavaModelException {
	if (requestor == null) {
		throw new IllegalArgumentException("Completion requestor cannot be null"); //$NON-NLS-1$
	}
	PerformanceStats performanceStats = CompletionEngine.PERF
		? PerformanceStats.getStats(JavaModelManager.COMPLETION_PERF, this)
		: null;
	if(performanceStats != null) {
		performanceStats.startRun(new String(cu.getFileName()) + " at " + position); //$NON-NLS-1$
	}
	IBuffer buffer = getBuffer();
	if (buffer == null) {
		return;
	}
	if (position < -1 || position > buffer.getLength()) {
		throw new JavaModelException(new JavaModelStatus(IJavaModelStatusConstants.INDEX_OUT_OF_BOUNDS));
	}
	JavaProject project = (JavaProject) getJavaProject();
	SearchableEnvironment environment = project.newSearchableNameEnvironment(owner);

	// set unit to skip
	environment.unitToSkip = unitToSkip;

	// code complete
	CompletionEngine engine = new CompletionEngine(environment, requestor, project.getOptions(true), project);
	engine.complete(cu, position, 0);
	if(performanceStats != null) {
		performanceStats.endRun();
	}
	if (NameLookup.VERBOSE) {
		System.out.println(Thread.currentThread() + " TIME SPENT in NameLoopkup#seekTypesInSourcePackage: " + environment.nameLookup.timeSpentInSeekTypesInSourcePackage + "ms");  //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println(Thread.currentThread() + " TIME SPENT in NameLoopkup#seekTypesInBinaryPackage: " + environment.nameLookup.timeSpentInSeekTypesInBinaryPackage + "ms");  //$NON-NLS-1$ //$NON-NLS-2$
	}
}
protected IJavaElement[] codeSelect(descent.internal.compiler.env.ICompilationUnit cu, int offset, int length, WorkingCopyOwner owner) throws JavaModelException {
	JavaProject project = (JavaProject)getJavaProject();
	
	SelectionEngine engine = new SelectionEngine(project.getOptions(true), project, owner);
	return engine.select(cu, offset, length);
}
protected IEvaluationResult codeEvaluate(descent.internal.compiler.env.ICompilationUnit cu, int offset, WorkingCopyOwner owner) throws JavaModelException {
	JavaProject project = (JavaProject)getJavaProject();
	
	EvaluationEngine engine = new EvaluationEngine(project.getOptions(true), project, owner);
	return engine.evaluate(cu, offset);
}
/*
 * Returns a new element info for this element.
 */
protected Object createElementInfo() {
	return new OpenableElementInfo();
}
/**
 * @see IJavaElement
 */
public boolean exists() {
	JavaModelManager manager = JavaModelManager.getJavaModelManager();
	if (manager.getInfo(this) != null) return true;
	if (!parentExists()) return false;
	PackageFragmentRoot root = getPackageFragmentRoot();
	if (root != null
			&& (root == this || !root.isArchive())) {
		return resourceExists();
	}
	return super.exists();
}
public String findRecommendedLineSeparator() throws JavaModelException {
	IBuffer buffer = getBuffer();
	String source = buffer == null ? null : buffer.getContents();
	return Util.getLineSeparator(source, getJavaProject());
}
protected void generateInfos(Object info, HashMap newElements, IProgressMonitor monitor) throws JavaModelException {

	if (JavaModelCache.VERBOSE){
		String element;
		switch (getElementType()) {
			case JAVA_PROJECT:
				element = "project"; //$NON-NLS-1$
				break;
			case PACKAGE_FRAGMENT_ROOT:
				element = "root"; //$NON-NLS-1$
				break;
			case PACKAGE_FRAGMENT:
				element = "package"; //$NON-NLS-1$
				break;
			case CLASS_FILE:
				element = "class file"; //$NON-NLS-1$
				break;
			case COMPILATION_UNIT:
				element = "compilation unit"; //$NON-NLS-1$
				break;
			default:
				element = "element"; //$NON-NLS-1$
		}
		System.out.println(Thread.currentThread() +" OPENING " + element + " " + this.toStringWithAncestors()); //$NON-NLS-1$//$NON-NLS-2$
	}
	
	// open the parent if necessary
	openParent(info, newElements, monitor);
	if (monitor != null && monitor.isCanceled()) 
		throw new OperationCanceledException();

	 // puts the info before building the structure so that questions to the handle behave as if the element existed
	 // (case of compilation units becoming working copies)
	newElements.put(this, info);

	// build the structure of the openable (this will open the buffer if needed)
	try {
		OpenableElementInfo openableElementInfo = (OpenableElementInfo)info;
		boolean isStructureKnown = buildStructure(openableElementInfo, monitor, newElements, getResource());
		openableElementInfo.setIsStructureKnown(isStructureKnown);
	} catch (JavaModelException e) {
		newElements.remove(this);
		throw e;
	}
	
	// remove out of sync buffer for this element
	JavaModelManager.getJavaModelManager().getElementsOutOfSynchWithBuffers().remove(this);

	if (JavaModelCache.VERBOSE) {
		System.out.println(JavaModelManager.getJavaModelManager().cacheToString("-> ")); //$NON-NLS-1$
	}
}
/**
 * Note: a buffer with no unsaved changes can be closed by the Java Model
 * since it has a finite number of buffers allowed open at one time. If this
 * is the first time a request is being made for the buffer, an attempt is
 * made to create and fill this element's buffer. If the buffer has been
 * closed since it was first opened, the buffer is re-created.
 * 
 * @see IOpenable
 */
public IBuffer getBuffer() throws JavaModelException {
	if (hasBuffer()) {
		// ensure element is open
		Object info = getElementInfo();
		IBuffer buffer = getBufferManager().getBuffer(this);
		if (buffer == null) {
			// try to (re)open a buffer
			buffer = openBuffer(null, info);
		}
		return buffer;
	} else {
		return null;
	}
}
/**
 * Answers the buffer factory to use for creating new buffers
 * @deprecated
 */
public IBufferFactory getBufferFactory(){
	return getBufferManager().getDefaultBufferFactory();
}

/**
 * Returns the buffer manager for this element.
 */
protected BufferManager getBufferManager() {
	return BufferManager.getDefaultBufferManager();
}
/**
 * Return my underlying resource. Elements that may not have a
 * corresponding resource must override this method.
 *
 * @see IJavaElement
 */
public IResource getCorrespondingResource() throws JavaModelException {
	return getUnderlyingResource();
}
/*
 * @see IJavaElement
 */
public IOpenable getOpenable() {
	return this;	
}



/**
 * @see IJavaElement
 */
public IResource getUnderlyingResource() throws JavaModelException {
	IResource parentResource = this.parent.getUnderlyingResource();
	if (parentResource == null) {
		return null;
	}
	int type = parentResource.getType();
	if (type == IResource.FOLDER || type == IResource.PROJECT) {
		IContainer folder = (IContainer) parentResource;
		IResource resource = folder.findMember(getElementName());
		if (resource == null) {
			throw newNotPresentException();
		} else {
			return resource;
		}
	} else {
		return parentResource;
	}
}

/**
 * Returns true if this element may have an associated source buffer,
 * otherwise false. Subclasses must override as required.
 */
protected boolean hasBuffer() {
	return false;
}
/**
 * @see IOpenable
 */
public boolean hasUnsavedChanges() throws JavaModelException{
	
	if (isReadOnly() || !isOpen()) {
		return false;
	}
	IBuffer buf = this.getBuffer();
	if (buf != null && buf.hasUnsavedChanges()) {
		return true;
	}
	// for package fragments, package fragment roots, and projects must check open buffers
	// to see if they have an child with unsaved changes
	int elementType = getElementType();
	if (elementType == PACKAGE_FRAGMENT ||
		elementType == PACKAGE_FRAGMENT_ROOT ||
		elementType == JAVA_PROJECT ||
		elementType == JAVA_MODEL) { // fix for 1FWNMHH
		Enumeration openBuffers= getBufferManager().getOpenBuffers();
		while (openBuffers.hasMoreElements()) {
			IBuffer buffer= (IBuffer)openBuffers.nextElement();
			if (buffer.hasUnsavedChanges()) {
				IJavaElement owner= (IJavaElement)buffer.getOwner();
				if (isAncestorOf(owner)) {
					return true;
				}
			}
		}
	}
	
	return false;
}
/**
 * Subclasses must override as required.
 *
 * @see IOpenable
 */
public boolean isConsistent() {
	return true;
}
/**
 * 
 * @see IOpenable
 */
public boolean isOpen() {
	return JavaModelManager.getJavaModelManager().getInfo(this) != null;
}
/**
 * Returns true if this represents a source element.
 * Openable source elements have an associated buffer created
 * when they are opened.
 */
protected boolean isSourceElement() {
	return false;
}
/**
 * @see IJavaElement
 */
public boolean isStructureKnown() throws JavaModelException {
	return ((OpenableElementInfo)getElementInfo()).isStructureKnown();
}
/**
 * @see IOpenable
 */
public void makeConsistent(IProgressMonitor monitor) throws JavaModelException {
	// only compilation units can be inconsistent
	// other openables cannot be inconsistent so default is to do nothing
}
/**
 * @see IOpenable
 */
public void open(IProgressMonitor pm) throws JavaModelException {
	getElementInfo(pm);
}

/**
 * Opens a buffer on the contents of this element, and returns
 * the buffer, or returns <code>null</code> if opening fails.
 * By default, do nothing - subclasses that have buffers
 * must override as required.
 */
protected IBuffer openBuffer(IProgressMonitor pm, Object info) throws JavaModelException {
	return null;
}

/**
 * Open the parent element if necessary.
 */
protected void openParent(Object childInfo, HashMap newElements, IProgressMonitor pm) throws JavaModelException {

	Openable openableParent = (Openable)getOpenableParent();
	if (openableParent != null && !openableParent.isOpen()){
		openableParent.generateInfos(openableParent.createElementInfo(), newElements, pm);
	}
}

/**
 *  Answers true if the parent exists (null parent is answering true)
 * 
 */
protected boolean parentExists(){
	
	IJavaElement parentElement = getParent();
	if (parentElement == null) return true;
	return parentElement.exists();
}

/**
 * Returns whether the corresponding resource or associated file exists
 */
protected boolean resourceExists() {
	IWorkspace workspace = ResourcesPlugin.getWorkspace();
	if (workspace == null) return false; // workaround for http://bugs.eclipse.org/bugs/show_bug.cgi?id=34069
	return 
		JavaModel.getTarget(
			workspace.getRoot(), 
			this.getPath().makeRelative(), // ensure path is relative (see http://dev.eclipse.org/bugs/show_bug.cgi?id=22517)
			true) != null;
}

/**
 * @see IOpenable
 */
public void save(IProgressMonitor pm, boolean force) throws JavaModelException {
	if (isReadOnly()) {
		throw new JavaModelException(new JavaModelStatus(IJavaModelStatusConstants.READ_ONLY, this));
	}
	IBuffer buf = getBuffer();
	if (buf != null) { // some Openables (like a JavaProject) don't have a buffer
		buf.save(pm, force);
		this.makeConsistent(pm); // update the element info of this element
	}
}

/**
 * Find enclosing package fragment root if any
 */
public PackageFragmentRoot getPackageFragmentRoot() {
	return (PackageFragmentRoot) getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
}

}
