package descent.internal.ui.javadocexport;

import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.IClasspathEntry;
import descent.core.IJavaProject;

import descent.ui.JavaUI;

import descent.internal.ui.wizards.buildpaths.BuildPathSupport;
import descent.internal.ui.wizards.buildpaths.CPListElement;


public class JavadocLinkRef {
	private final IJavaProject fProject;
	private final IPath fContainerPath;
	private IClasspathEntry fClasspathEntry;
	
	public JavadocLinkRef(IPath containerPath, IClasspathEntry classpathEntry, IJavaProject project) {
		fContainerPath= containerPath;
		fProject= project;
		fClasspathEntry= classpathEntry;
	}
	
	public JavadocLinkRef(IJavaProject project) {
		this(null, null, project);
	}
	
	public boolean isProjectRef() {
		return fClasspathEntry == null;
	}
	
	public IPath getFullPath() {
		return isProjectRef() ? fProject.getPath() : fClasspathEntry.getPath();
	}
	
	public URL getURL() {
		if (isProjectRef()) {
			return JavaUI.getProjectJavadocLocation(fProject);
		} else {
			return JavaUI.getLibraryJavadocLocation(fClasspathEntry);
		}
	}
	
	public void setURL(URL url, IProgressMonitor monitor) throws CoreException {
		if (isProjectRef()) {
			JavaUI.setProjectJavadocLocation(fProject, url);
		} else {
			CPListElement element= CPListElement.createFromExisting(fClasspathEntry, fProject);
			String location= url != null ? url.toExternalForm() : null;
			element.setAttribute(CPListElement.JAVADOC, location);
			String[] changedAttributes= { CPListElement.JAVADOC };
			BuildPathSupport.modifyClasspathEntry(null, element.getClasspathEntry(), changedAttributes, fProject, fContainerPath, monitor);
			fClasspathEntry= element.getClasspathEntry();
		}
	}
	
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass().equals(getClass())) {
			JavadocLinkRef other= (JavadocLinkRef) obj;
			if (!fProject.equals(other.fProject) || isProjectRef() != other.isProjectRef()) {
				return false;
			}
			if (!isProjectRef()) {
				return !fClasspathEntry.equals(other.fClasspathEntry);
			}
		}
		return false;
	}
	
	public int hashCode() {
		if (isProjectRef()) {
			return fProject.hashCode();
		} else {
			return fProject.hashCode() + fClasspathEntry.hashCode();
		}

	}
}
