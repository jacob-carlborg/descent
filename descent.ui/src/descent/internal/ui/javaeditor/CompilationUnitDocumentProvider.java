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

package descent.internal.ui.javaeditor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ILineTracker;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

import descent.core.IBuffer;
import descent.core.IClasspathEntry;
import descent.core.ICompilationUnit;
import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.IProblemRequestor;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.internal.corext.util.JavaModelUtil;
import descent.ui.DescentUI;

// TODO JDT implement with real one, this is just a temporary stub
public class CompilationUnitDocumentProvider extends TextFileDocumentProvider implements ICompilationUnitDocumentProvider {
	
	/**
	 * Element information of all connected elements with a fake CU but no file info.
	 * @since 3.2
	 */
	private final Map<Object, CompilationUnitInfo> fFakeCUMapForMissingInfo= new HashMap<Object, CompilationUnitInfo>();
	
	/**
	 * Bundle of all required informations to allow working copy management.
	 */
	static protected class CompilationUnitInfo extends FileInfo {
		public ICompilationUnit fCopy;
	}

	public void addGlobalAnnotationModelListener(IAnnotationModelListener listener) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * @see descent.internal.ui.javaeditor.ICompilationUnitDocumentProvider#createLineTracker(java.lang.Object)
	 */
	public ILineTracker createLineTracker(Object element) {
		return new DefaultLineTracker();
	}

	public ICompilationUnit getWorkingCopy(Object element) {
		FileInfo fileInfo= getFileInfo(element);
		if (fileInfo instanceof CompilationUnitInfo) {
			CompilationUnitInfo info= (CompilationUnitInfo)fileInfo;
			return info.fCopy;
		}
		CompilationUnitInfo cuInfo= (CompilationUnitInfo)fFakeCUMapForMissingInfo.get(element);
		if (cuInfo != null)
			return cuInfo.fCopy;
		
		return null;
	}

	public void removeGlobalAnnotationModelListener(IAnnotationModelListener listener) {
		// TODO Auto-generated method stub
		
	}

	public void saveDocumentContent(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	public void setSavePolicy(ISavePolicy savePolicy) {
		// TODO Auto-generated method stub
		
	}

	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * @see org.eclipse.ui.editors.text.TextFileDocumentProvider#connect(java.lang.Object)
	 * @since 3.2
	 */
	public void connect(Object element) throws CoreException {
		super.connect(element);
		if (getFileInfo(element) != null)
			return;

		CompilationUnitInfo info= (CompilationUnitInfo)fFakeCUMapForMissingInfo.get(element);
		if (info == null) {
			ICompilationUnit cu= createFakeCompiltationUnit(element, true);
			if (cu == null)
				return;
			info= new CompilationUnitInfo();
			info.fCopy= cu;
			info.fElement= element;
			info.fModel= new AnnotationModel();
			fFakeCUMapForMissingInfo.put(element, info);
		}
		info.fCount++;
	}
	
	/**
	 * Creates a fake compilation unit.
	 *
	 * @param element the element
	 * @param setContents tells whether to read and set the contents to the new CU
	 * @since 3.2
	 */
	private ICompilationUnit createFakeCompiltationUnit(Object element, boolean setContents) {
		if (!(element instanceof IStorageEditorInput))
			return null;
		
		final IStorageEditorInput sei= (IStorageEditorInput)element;
		
		try {
			final IStorage storage= sei.getStorage();
			final IPath storagePath= storage.getFullPath();
			if (storage.getName() == null || storagePath == null)
				return null;
			
			final IPath documentPath;
			if (storage instanceof IFileState)
				documentPath= storagePath.append(Long.toString(((IFileState)storage).getModificationTime()));
			else
				documentPath= storagePath;
			
			WorkingCopyOwner woc= new WorkingCopyOwner() {
				/*
				 * @see descent.core.WorkingCopyOwner#createBuffer(descent.core.ICompilationUnit)
				 * @since 3.2
				 */
				public IBuffer createBuffer(ICompilationUnit workingCopy) {
					return new DocumentAdapter(workingCopy, documentPath);
				}
			};

			IClasspathEntry[] cpEntries= null;
			IJavaProject jp= findJavaProject(storagePath);
			if (jp != null)
				cpEntries= jp.getResolvedClasspath(true);
			
			if (cpEntries == null || cpEntries.length == 0) {
				// TODO JDT cpEntries= new IClasspathEntry[] { JavaRuntime.getDefaultJREContainerEntry() };
				cpEntries = new IClasspathEntry[0];
			}

			final ICompilationUnit cu= woc.newWorkingCopy(storage.getName(), cpEntries, null, getProgressMonitor());
			if (setContents) {
				int READER_CHUNK_SIZE= 2048;
				int BUFFER_SIZE= 8 * READER_CHUNK_SIZE;
				Reader in= new BufferedReader(new InputStreamReader(storage.getContents()));
				StringBuffer buffer= new StringBuffer(BUFFER_SIZE);
				char[] readBuffer= new char[READER_CHUNK_SIZE];
				int n;
				try {
					n= in.read(readBuffer);
					while (n > 0) {
						buffer.append(readBuffer, 0, n);
						n= in.read(readBuffer);
					}
				} catch (IOException e) {
					DescentUI.log(e);
				}
				cu.getBuffer().setContents(buffer.toString());
			}
			
			if (!isModifiable(element))
				JavaModelUtil.reconcile(cu);
			
			return cu;
		} catch (CoreException ex) {
			return null;
		}
	}
	
	/**
	 * Fuzzy search for Java project in the workspace that matches
	 * the given path.
	 * 
	 * @param path the path to match
	 * @return the matching Java project or <code>null</code>
	 * @since 3.2
	 */
	private IJavaProject findJavaProject(IPath path) {
		if (path == null)
			return null;

		String[] pathSegments= path.segments();
		IJavaModel model= JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
		IJavaProject[] projects;
		try {
			projects= model.getJavaProjects();
		} catch (JavaModelException e) {
			return null; // ignore - use default JRE
		}
		for (int i= 0; i < projects.length; i++) {
			IPath projectPath= projects[i].getProject().getFullPath();
			String projectSegment= projectPath.segments()[0];
			for (int j= 0; j < pathSegments.length; j++)
				if (projectSegment.equals(pathSegments[j]))
					return projects[i];
		}
		return null;
	}
	
	/*
	 * @see org.eclipse.ui.editors.text.TextFileDocumentProvider#createFileInfo(java.lang.Object)
	 */
	protected FileInfo createFileInfo(Object element) throws CoreException {
		ICompilationUnit original= null;
		if (element instanceof IFileEditorInput) {
			IFileEditorInput input= (IFileEditorInput) element;
			original= createCompilationUnit(input.getFile());
			if (original == null)
				return null;
		}
		
		FileInfo info= super.createFileInfo(element);
		if (!(info instanceof CompilationUnitInfo))
			return null;
		
		if (original == null)
			original= createFakeCompiltationUnit(element, false);
		if (original == null)
			return null;

		CompilationUnitInfo cuInfo= (CompilationUnitInfo) info;
		setUpSynchronization(cuInfo);

		IProblemRequestor requestor= cuInfo.fModel instanceof IProblemRequestor ? (IProblemRequestor) cuInfo.fModel : null;
		/* TODO JDT
		if (requestor instanceof IProblemRequestorExtension) {
			IProblemRequestorExtension extension= (IProblemRequestorExtension) requestor;
			extension.setIsActive(false);
			extension.setIsHandlingTemporaryProblems(isHandlingTemporaryProblems());
		}
		*/

		if (JavaModelUtil.isPrimary(original))
			original.becomeWorkingCopy(requestor, getProgressMonitor());
		cuInfo.fCopy= original;

		/* TODO JDT
		if (cuInfo.fModel instanceof CompilationUnitAnnotationModel)   {
			CompilationUnitAnnotationModel model= (CompilationUnitAnnotationModel) cuInfo.fModel;
			model.setCompilationUnit(cuInfo.fCopy);
		}

		if (cuInfo.fModel != null)
			cuInfo.fModel.addAnnotationModelListener(fGlobalAnnotationModelListener);
		*/

		return cuInfo;
	}
	
	/**
	 * Creates a compilation unit from the given file.
	 *
	 * @param file the file from which to create the compilation unit
	 */
	protected ICompilationUnit createCompilationUnit(IFile file) {
		Object element= JavaCore.create(file);
		if (element instanceof ICompilationUnit)
			return (ICompilationUnit) element;
		return null;
	}
	

}
