package descent.internal.ui.preferences;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;

import descent.core.IClasspathEntry;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IPackageFragmentRoot;
import descent.core.JavaCore;
import descent.core.JavaModelException;

import descent.internal.corext.util.JavaModelUtil;

import descent.internal.ui.JavaPlugin;
import descent.internal.ui.dialogs.StatusUtil;
import descent.internal.ui.util.ExceptionHandler;
import descent.internal.ui.wizards.IStatusChangeListener;
import descent.internal.ui.wizards.buildpaths.ArchiveFileFilter;
import descent.internal.ui.wizards.buildpaths.BuildPathSupport;
import descent.internal.ui.wizards.buildpaths.CPListElement;

public class NativeLibrariesPropertyPage extends PropertyPage implements IStatusChangeListener {

	private NativeLibrariesConfigurationBlock fConfigurationBlock;
	private boolean fIsValidElement;
	private IClasspathEntry fEntry;
	private IPath fContainerPath;
	
	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		IJavaElement elem= getJavaElement();
		try {
			if (elem instanceof IPackageFragmentRoot) {
				IPackageFragmentRoot root= (IPackageFragmentRoot) elem;
				
				IClasspathEntry entry= root.getRawClasspathEntry();
				if (entry == null) {
					fIsValidElement= false;
				} else {
					if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
						fContainerPath= entry.getPath();
						fEntry= JavaModelUtil.getClasspathEntryToEdit(elem.getJavaProject(), fContainerPath, root.getPath());
						fIsValidElement= fEntry != null;
					} else {
						fContainerPath= null;
						fEntry= entry;
						fIsValidElement= true;
					}
				}
			} else {
				fIsValidElement= false;
			}
		} catch (JavaModelException e) {
			fIsValidElement= false;
		}
		if (!fIsValidElement) {
			setDescription(PreferencesMessages.NativeLibrariesPropertyPage_invalidElementSelection_desription); 
		}
		super.createControl(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	protected Control createContents(Composite parent) {
		if (!fIsValidElement)
			return new Composite(parent, SWT.NONE);
		
		IJavaElement elem= getJavaElement();
		if (elem == null)
			return new Composite(parent, SWT.NONE);
		
		CPListElement cpElement= CPListElement.createFromExisting(fEntry, elem.getJavaProject());
		fConfigurationBlock= new NativeLibrariesConfigurationBlock(this, getShell(), cpElement);
		Control control= fConfigurationBlock.createContents(parent);
		control.setVisible(elem != null);

		Dialog.applyDialogFont(control);
		return control;
	}

	/**
	 * {@inheritDoc}
	 */
	public void statusChanged(IStatus status) {
		setValid(!status.matches(IStatus.ERROR));
		StatusUtil.applyToStatusLine(this, status);
	}
	
	/*
	 * @see PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		if (fConfigurationBlock != null) {
			fConfigurationBlock.performDefaults();
		}
		super.performDefaults();
	}
	
	/**
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		if (fConfigurationBlock != null) {
			String nativeLibraryPath= fConfigurationBlock.getNativeLibraryPath();
			if (nativeLibraryPath == null) {
				return true;//no change
			}
			
			IJavaElement elem= getJavaElement();
			try {
				IRunnableWithProgress runnable= getRunnable(getShell(), elem, nativeLibraryPath, fEntry, fContainerPath);
				PlatformUI.getWorkbench().getProgressService().run(true, true, runnable);
			} catch (InvocationTargetException e) {
				String title= PreferencesMessages.NativeLibrariesPropertyPage_errorAttaching_title; 
				String message= PreferencesMessages.NativeLibrariesPropertyPage_errorAttaching_message; 
				ExceptionHandler.handle(e, getShell(), title, message);
				return false;
			} catch (InterruptedException e) {
				// Canceled
				return false;
			}
		}
		return true;
	}
	
	private static IRunnableWithProgress getRunnable(final Shell shell, final IJavaElement elem, final String nativeLibraryPath, final IClasspathEntry entry, final IPath containerPath) {
		return new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {				
				try {
					IJavaProject project= elem.getJavaProject();
					if (elem instanceof IPackageFragmentRoot) {
						CPListElement cpElem= CPListElement.createFromExisting(entry, project);
						cpElem.setAttribute(CPListElement.NATIVE_LIB_PATH, nativeLibraryPath);
						IClasspathEntry newEntry= cpElem.getClasspathEntry();
						String[] changedAttributes= { CPListElement.NATIVE_LIB_PATH };
						BuildPathSupport.modifyClasspathEntry(shell, newEntry, changedAttributes, project, containerPath, monitor);
					}
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				}
			}
		};
	}
	
	private IJavaElement getJavaElement() {
		IAdaptable adaptable= getElement();
		IJavaElement elem= (IJavaElement) adaptable.getAdapter(IJavaElement.class);
		if (elem == null) {

			IResource resource= (IResource) adaptable.getAdapter(IResource.class);
			//special case when the .jar is a file
			try {
				if (resource instanceof IFile && ArchiveFileFilter.isArchivePath(resource.getFullPath())) {
					IProject proj= resource.getProject();
					if (proj.hasNature(JavaCore.NATURE_ID)) {
						IJavaProject jproject= JavaCore.create(proj);
						elem= jproject.getPackageFragmentRoot(resource); // create a handle
					}
				}
			} catch (CoreException e) {
				JavaPlugin.log(e);
			}
		}
		return elem;
	}

}
