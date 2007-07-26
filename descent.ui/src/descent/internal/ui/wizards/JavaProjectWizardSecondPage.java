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
package descent.internal.ui.wizards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;

import descent.core.IClasspathEntry;
import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.internal.corext.util.JavaModelUtil;
import descent.internal.corext.util.Messages;
import descent.internal.ui.util.CoreUtility;
import descent.internal.ui.util.ExceptionHandler;
import descent.launching.IVMInstall;
import descent.launching.JavaRuntime;
import descent.ui.JavaUI;
import descent.ui.PreferenceConstants;
import descent.ui.wizards.JavaCapabilityConfigurationPage;

/**
 * As addition to the JavaCapabilityConfigurationPage, the wizard does an
 * early project creation (so that linked folders can be defined) and, if an
 * existing external location was specified, offers to do a classpath detection
 */
public class JavaProjectWizardSecondPage extends JavaCapabilityConfigurationPage {

	private static final String FILENAME_PROJECT= ".project"; //$NON-NLS-1$
	private static final String FILENAME_CLASSPATH= ".classpath"; //$NON-NLS-1$

	private final JavaProjectWizardFirstPage fFirstPage;

	private URI fCurrProjectLocation; // null if location is platform location
	private IProject fCurrProject;
	
	private boolean fKeepContent;

	private File fDotProjectBackup;
	private File fDotClasspathBackup;
	private Boolean fIsAutobuild;

	/**
	 * Constructor for JavaProjectWizardSecondPage.
	 */
	public JavaProjectWizardSecondPage(JavaProjectWizardFirstPage mainPage) {
		fFirstPage= mainPage;
		fCurrProjectLocation= null;
		fCurrProject= null;
		fKeepContent= false;
		
		fDotProjectBackup= null;
		fDotClasspathBackup= null;
		fIsAutobuild= null;
	}
	
	protected boolean useNewSourcePage() {
		return true;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		if (visible) {
			changeToNewProject();
		} else {
			removeProject();
		}
		super.setVisible(visible);
	}
	
	private void changeToNewProject() {
		fKeepContent= fFirstPage.getDetect();
		
		final IRunnableWithProgress op= new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					if (fIsAutobuild == null) {
						fIsAutobuild= Boolean.valueOf(CoreUtility.enableAutoBuild(false));
					}
                    updateProject(monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} catch (OperationCanceledException e) {
					throw new InterruptedException();
				} finally {
                    monitor.done();
                }
			}
		};
	
		try {
			getContainer().run(true, false, new WorkspaceModifyDelegatingOperation(op));
		} catch (InvocationTargetException e) {
			final String title= NewWizardMessages.JavaProjectWizardSecondPage_error_title; 
			final String message= NewWizardMessages.JavaProjectWizardSecondPage_error_message; 
			ExceptionHandler.handle(e, getShell(), title, message);
		} catch  (InterruptedException e) {
			// cancel pressed
		}
	}
	
	final void updateProject(IProgressMonitor monitor) throws CoreException, InterruptedException {
		
		fCurrProject= fFirstPage.getProjectHandle();
		fCurrProjectLocation= getProjectLocationURI(); 
		
		if (monitor == null) {
			monitor= new NullProgressMonitor();
		}
		try {
			monitor.beginTask(NewWizardMessages.JavaProjectWizardSecondPage_operation_initialize, 7); 
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			
			URI realLocation= fCurrProjectLocation;
			if (fCurrProjectLocation == null) {  // inside workspace
				try {
					URI rootLocation= ResourcesPlugin.getWorkspace().getRoot().getLocationURI();
					realLocation= new URI(rootLocation.getScheme(), null,
						Path.fromPortableString(rootLocation.getPath()).append(fCurrProject.getName()).toString(),
						null);
				} catch (URISyntaxException e) {
					Assert.isTrue(false, "Can't happen"); //$NON-NLS-1$
				}
			}

			rememberExistingFiles(realLocation);
            
			createProject(fCurrProject, fCurrProjectLocation, new SubProgressMonitor(monitor, 2));
				
			IClasspathEntry[] entries= null;
			IPath outputLocation= null;
	
			if (fFirstPage.getDetect()) {
				if (!fCurrProject.getFile(FILENAME_CLASSPATH).exists()) { 
					final ClassPathDetector detector= new ClassPathDetector(fCurrProject, new SubProgressMonitor(monitor, 2));
					entries= detector.getClasspath();
                    outputLocation= detector.getOutputLocation();
				} else {
					monitor.worked(2);
				}
			} else if (fFirstPage.isSrcBin()) {
				IPreferenceStore store= PreferenceConstants.getPreferenceStore();
				IPath srcPath= new Path(store.getString(PreferenceConstants.SRCBIN_SRCNAME));
				IPath binPath= new Path(store.getString(PreferenceConstants.SRCBIN_BINNAME));
				
				if (srcPath.segmentCount() > 0) {
					IFolder folder= fCurrProject.getFolder(srcPath);
					CoreUtility.createFolder(folder, true, true, new SubProgressMonitor(monitor, 1));
				} else {
					monitor.worked(1);
				}
				
				if (binPath.segmentCount() > 0 && !binPath.equals(srcPath)) {
					IFolder folder= fCurrProject.getFolder(binPath);
					CoreUtility.createFolder(folder, true, true, new SubProgressMonitor(monitor, 1));
				} else {
					monitor.worked(1);
				}
				
				final IPath projectPath= fCurrProject.getFullPath();

				// configure the classpath entries, including the default jre library.
				List cpEntries= new ArrayList();
				cpEntries.add(JavaCore.newSourceEntry(projectPath.append(srcPath)));
				cpEntries.addAll(Arrays.asList(getDefaultClasspathEntry()));
				entries= (IClasspathEntry[]) cpEntries.toArray(new IClasspathEntry[cpEntries.size()]);
				
				// configure the output location
				outputLocation= projectPath.append(binPath);
			} else {
				IPath projectPath= fCurrProject.getFullPath();
				List cpEntries= new ArrayList();
				cpEntries.add(JavaCore.newSourceEntry(projectPath));
				cpEntries.addAll(Arrays.asList(getDefaultClasspathEntry()));
				entries= (IClasspathEntry[]) cpEntries.toArray(new IClasspathEntry[cpEntries.size()]);

				outputLocation= projectPath;
				monitor.worked(2);
			}
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			
            init(JavaCore.create(fCurrProject), outputLocation, entries, false);
			configureJavaProject(new SubProgressMonitor(monitor, 3)); // create the Java project to allow the use of the new source folder page
		} finally {
			monitor.done();
		}
	}
	
	private URI getProjectLocationURI() throws CoreException {
		if (fFirstPage.isInWorkspace()) {
			return null;
		}
		return URIUtil.toURI(fFirstPage.getLocationPath());
	}
	
	private IClasspathEntry[] getDefaultClasspathEntry() {
		IClasspathEntry[] defaultJRELibrary= PreferenceConstants.getDefaultJRELibrary();
		String compliance= fFirstPage.getCompilerCompliance();
		IPath jreContainerPath= new Path(JavaRuntime.JRE_CONTAINER);
		if (compliance == null || defaultJRELibrary.length > 1 || !jreContainerPath.isPrefixOf(defaultJRELibrary[0].getPath())) {
			// use default
			return defaultJRELibrary;
		}
		IVMInstall inst= fFirstPage.getJVM();
		if (inst != null) {
			IPath newPath= jreContainerPath.append(inst.getVMInstallType().getId()).append(inst.getName());
			return new IClasspathEntry[] { JavaCore.newContainerEntry(newPath) };
		}
		return defaultJRELibrary;
	}
	
	private void rememberExistingFiles(URI projectLocation) throws CoreException {
		fDotProjectBackup= null;
		fDotClasspathBackup= null;
		
		IFileStore file= EFS.getStore(projectLocation);
		if (file.fetchInfo().exists()) {
			IFileStore projectFile= file.getChild(FILENAME_PROJECT);
			if (projectFile.fetchInfo().exists()) {
				fDotProjectBackup= createBackup(projectFile, "project-desc"); //$NON-NLS-1$ 
			}
			IFileStore classpathFile= file.getChild(FILENAME_CLASSPATH);
			if (classpathFile.fetchInfo().exists()) {
				fDotClasspathBackup= createBackup(classpathFile, "classpath-desc"); //$NON-NLS-1$ 
			}
		}
	}
	
	private void restoreExistingFiles(URI projectLocation, IProgressMonitor monitor) throws CoreException {
		int ticks= ((fDotProjectBackup != null ? 1 : 0) + (fDotClasspathBackup != null ? 1 : 0)) * 2;
		monitor.beginTask("", ticks); //$NON-NLS-1$
		try {
			if (fDotProjectBackup != null) {
				IFileStore projectFile= EFS.getStore(projectLocation).getChild(FILENAME_PROJECT);
				projectFile.delete(EFS.NONE, new SubProgressMonitor(monitor, 1));
				copyFile(fDotProjectBackup, projectFile, new SubProgressMonitor(monitor, 1));
			}
		} catch (IOException e) {
			IStatus status= new Status(IStatus.ERROR, JavaUI.ID_PLUGIN, IStatus.ERROR, NewWizardMessages.JavaProjectWizardSecondPage_problem_restore_project, e); 
			throw new CoreException(status);
		}
		try {
			if (fDotClasspathBackup != null) {
				IFileStore classpathFile= EFS.getStore(projectLocation).getChild(FILENAME_CLASSPATH);
				classpathFile.delete(EFS.NONE, new SubProgressMonitor(monitor, 1));
				copyFile(fDotClasspathBackup, classpathFile, new SubProgressMonitor(monitor, 1));
			}
		} catch (IOException e) {
			IStatus status= new Status(IStatus.ERROR, JavaUI.ID_PLUGIN, IStatus.ERROR, NewWizardMessages.JavaProjectWizardSecondPage_problem_restore_classpath, e); 
			throw new CoreException(status);
		}
	}
	
	private File createBackup(IFileStore source, String name) throws CoreException {
		try {
			File bak= File.createTempFile("eclipse-" + name, ".bak");  //$NON-NLS-1$//$NON-NLS-2$
			copyFile(source, bak);
			return bak;
		} catch (IOException e) {
			IStatus status= new Status(IStatus.ERROR, JavaUI.ID_PLUGIN, IStatus.ERROR, Messages.format(NewWizardMessages.JavaProjectWizardSecondPage_problem_backup, name), e); 
			throw new CoreException(status);
		} 
	}
	
	private void copyFile(IFileStore source, File target) throws IOException, CoreException {
		InputStream is= source.openInputStream(EFS.NONE, null);
		FileOutputStream os= new FileOutputStream(target);
		copyFile(is, os);
	}
	
	private void copyFile(File source, IFileStore target, IProgressMonitor monitor) throws IOException, CoreException {
		FileInputStream is= new FileInputStream(source);
		OutputStream os= target.openOutputStream(EFS.NONE, monitor);
		copyFile(is, os);
	}
	
	private void copyFile(InputStream is, OutputStream os) throws IOException {		
		try {
			byte[] buffer = new byte[8192];
			while (true) {
				int bytesRead= is.read(buffer);
				if (bytesRead == -1)
					break;
				
				os.write(buffer, 0, bytesRead);
			}
		} finally {
			try {
				is.close();
			} finally {
				os.close();
			}
		}
	}
	
	/**
	 * Called from the wizard on finish.
	 */
	public void performFinish(IProgressMonitor monitor) throws CoreException, InterruptedException {
		try {
			monitor.beginTask(NewWizardMessages.JavaProjectWizardSecondPage_operation_create, 3); 
			if (fCurrProject == null) {
				updateProject(new SubProgressMonitor(monitor, 1));
			}
			configureJavaProject(new SubProgressMonitor(monitor, 2));
			
			if (!fKeepContent) {
				String compliance= fFirstPage.getCompilerCompliance();
				if (compliance != null) {
					IJavaProject project= JavaCore.create(fCurrProject);
					Map options= project.getOptions(false);
					JavaModelUtil.setCompilanceOptions(options, compliance);
					project.setOptions(options);
				}
			}
		} finally {
			monitor.done();
			fCurrProject= null;
			if (fIsAutobuild != null) {
				CoreUtility.enableAutoBuild(fIsAutobuild.booleanValue());
				fIsAutobuild= null;
			}
		}
	}

	private void removeProject() { 
		if (fCurrProject == null || !fCurrProject.exists()) {
			return;
		}
		
		IRunnableWithProgress op= new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				doRemoveProject(monitor);
			}
		};
	
		try {
			getContainer().run(true, true, new WorkspaceModifyDelegatingOperation(op));
		} catch (InvocationTargetException e) {
			final String title= NewWizardMessages.JavaProjectWizardSecondPage_error_remove_title; 
			final String message= NewWizardMessages.JavaProjectWizardSecondPage_error_remove_message; 
			ExceptionHandler.handle(e, getShell(), title, message);		
		} catch  (InterruptedException e) {
			// cancel pressed
		}
	}
	
	final void doRemoveProject(IProgressMonitor monitor) throws InvocationTargetException {
		final boolean noProgressMonitor= (fCurrProjectLocation == null); // inside workspace
		if (monitor == null || noProgressMonitor) {
			monitor= new NullProgressMonitor();
		}
		monitor.beginTask(NewWizardMessages.JavaProjectWizardSecondPage_operation_remove, 3); 
		try {
			try {
				URI projLoc= fCurrProject.getLocationURI();
				
			    boolean removeContent= !fKeepContent && fCurrProject.isSynchronized(IResource.DEPTH_INFINITE);
			    fCurrProject.delete(removeContent, false, new SubProgressMonitor(monitor, 2));
				
				restoreExistingFiles(projLoc, new SubProgressMonitor(monitor, 1));
			} finally {
				CoreUtility.enableAutoBuild(fIsAutobuild.booleanValue()); // fIsAutobuild must be set
				fIsAutobuild= null;
			}
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		} finally {
			monitor.done();
			fCurrProject= null;
			fKeepContent= false;
		}
	}

	/**
	 * Called from the wizard on cancel.
	 */
	public void performCancel() {
		removeProject();
	}      
 }
