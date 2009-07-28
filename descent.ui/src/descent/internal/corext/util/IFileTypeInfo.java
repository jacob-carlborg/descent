package descent.internal.corext.util;

import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

import descent.core.IJavaElement;
import descent.core.JavaCore;
import descent.core.search.IJavaSearchScope;

/**
 * A <tt>IFileTypeInfo</tt> represents a type in a class or java file.
 */
public class IFileTypeInfo extends TypeInfo {
	
	private final String fProject;
	private final String fFolder;
	private final String fFile;
	private final String fExtension;
	private final String fPath;
	
	public IFileTypeInfo(String pkg, String name, char[][] enclosingTypes, long modifiers, int kind, String project, String sourceFolder, String file, String extension, String path) {
		super(pkg, name, enclosingTypes, modifiers, kind);
		fProject= project;
		fFolder= sourceFolder;
		fFile= file;
		fExtension= extension;
		fPath= path;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!IFileTypeInfo.class.equals(obj.getClass()))
			return false;
		IFileTypeInfo other= (IFileTypeInfo)obj;
		return doEquals(other) && fProject.equals(other.fProject) && equals(fFolder, other.fFolder) &&
			fFile.equals(other.fFile) && fExtension.equals(other.fExtension);
	}
	
	public int getElementType() {
		return TypeInfo.IFILE_TYPE_INFO;
	}
	
	protected IJavaElement getContainer(IJavaSearchScope scope) {
		IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
		IPath path= new Path(getPath());
		IResource resource= root.findMember(path);
		if (resource != null) {
			IJavaElement elem= JavaCore.create(resource);
			if (elem != null && elem.exists()) {
				return elem;
			}
		}
		return null;
	}
	
	public IPath getPackageFragmentRootPath() {
		StringBuffer buffer= new StringBuffer();
		buffer.append(TypeInfo.SEPARATOR);
		buffer.append(fProject);
		if (fFolder != null && fFolder.length() > 0) {
			buffer.append(TypeInfo.SEPARATOR);
			buffer.append(fFolder);
		}
		return new Path(buffer.toString());
	}
	
	public String getPackageFragmentRootName() {
		StringBuffer buffer= new StringBuffer();
		buffer.append(fProject);
		if (fFolder != null && fFolder.length() > 0) {
			buffer.append(TypeInfo.SEPARATOR);
			buffer.append(fFolder);
		}
		return buffer.toString();
	}
		
	public String getPath() {
		/*
		StringBuffer result= new StringBuffer();
		result.append(TypeInfo.SEPARATOR);
		result.append(fProject);
		result.append(TypeInfo.SEPARATOR);
		if (fFolder != null && fFolder.length() > 0) {
				result.append(fFolder);
				result.append(TypeInfo.SEPARATOR);
		}
		if (fPackage != null && fPackage.length() > 0) {
			result.append(fPackage.replace(TypeInfo.PACKAGE_PART_SEPARATOR, TypeInfo.SEPARATOR));
			//result.append(TypeInfo.SEPARATOR);
		}
		//result.append(fFile);
		result.append('.');
		result.append(fExtension);
		return result.toString();
		*/
		return fPath;
	}
	
	public String getProject() {
		return fProject;
	}
	
	public String getFolder() {
		return fFolder;
	}
	
	public String getFileName() {
		return fFile;
	}
	
	public String getExtension() {
		return fExtension;
	}
	
	public long getContainerTimestamp() {
		IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
		IPath path= new Path(getPath());
		IResource resource= root.findMember(path);
		if (resource != null) {
			URI location= resource.getLocationURI();
			if (location != null) {
				try {
					IFileInfo info= EFS.getStore(location).fetchInfo();
					if (info.exists()) {
						// The element could be removed from the build path. So check
						// if the Java element still exists.
						IJavaElement element= JavaCore.create(resource);
						if (element != null && element.exists())
							return info.getLastModified();
					}
				} catch (CoreException e) {
					// Fall through
				}
			}
		}
		return IResource.NULL_STAMP;
	}
	
	public boolean isContainerDirty() {
		IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
		IPath path= new Path(getPath());
		IResource resource= root.findMember(path);
		ITextFileBufferManager manager= FileBuffers.getTextFileBufferManager();
		ITextFileBuffer textFileBuffer= manager.getTextFileBuffer(resource.getFullPath());
		if (textFileBuffer != null) {
			return textFileBuffer.isDirty();
		}
		return false;
	}
}
