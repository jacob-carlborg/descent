package descent.internal.core.ctfe;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.DirectorySourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.FolderSourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.ProjectSourceContainer;

import descent.core.ICompilationUnit;

public class DescentCtfeSourceLookupParticipant extends AbstractSourceLookupParticipant {
	
	public String getSourceName(Object object) throws CoreException {
		if (object instanceof DescentCtfeStackFrame) {
			return ((DescentCtfeStackFrame) object).getCompilationUnit().getFullyQualifiedName();
		}
		return null;
	}
	
	@Override
	public Object[] findSourceElements(Object object) throws CoreException {
		if (object instanceof DescentCtfeStackFrame) {
			DescentCtfeStackFrame frame = (DescentCtfeStackFrame) object;
			ICompilationUnit unit = frame.getCompilationUnit();
			IResource resource = unit.getResource();
			if (resource != null) {
				return new Object[] { resource };	
			}
			
			String sourceName = unit.getPath().toOSString();
			
			// If it's a full path, see if we can match a source container's
			// full path. TODO this is a ugly hack
			File file = new File(sourceName);
			if (!file.isAbsolute()) {
				return null;
			}
			
			sourceName = file.getAbsolutePath();
			
			ISourceContainer[] containers = getSourceContainers();
			Object[] possible = findSourceElements(sourceName, containers);
			if (possible != null) {
				return possible;
			}
		}
		return null;
	}
	
	private Object[] findSourceElements(String sourceName, ISourceContainer[] containers) throws CoreException {
		for(ISourceContainer container : containers) {
			String typeId = container.getType().getId();
			
			if (ProjectSourceContainer.TYPE_ID.equals(typeId)) {
				ProjectSourceContainer projectContainer = (ProjectSourceContainer) container;
				IProject project = projectContainer.getProject();
				Object[] possible = findSourceElements(sourceName, project);
				if (possible != null) {
					return possible;
				}
			}
			
			if (FolderSourceContainer.TYPE_ID.equals(typeId)) {
				FolderSourceContainer folderContainer = (FolderSourceContainer) container;
				IContainer containerObj = folderContainer.getContainer();
				Object[] possible = findSourceElements(sourceName, containerObj);
				if (possible != null) {
					return possible;
				}
			}
			
			if (DirectorySourceContainer.TYPE_ID.equals(typeId)) {
				DirectorySourceContainer directoryContainer = (DirectorySourceContainer) container;
				Object[] possible = findSourceElements(sourceName, directoryContainer);
				if (possible != null) {
					return possible;
				}
			}
			
			if (container.isComposite()) {
				Object[] possible = findSourceElements(sourceName, container.getSourceContainers());
				if (possible != null) {
					return possible;
				}
			}
		}
		
		return null;
	}
	
	private Object[] findSourceElements(String sourceName, IContainer container) {
		File containerFile = container.getLocation().toFile();
		String containerAbsolutePath = containerFile.getAbsolutePath();
		if (sourceName.startsWith(containerAbsolutePath)) {
			String relativePathStr = sourceName.substring(containerAbsolutePath.length());
			Path relativePathObj = new Path(relativePathStr);
			return new Object[] { container.getFile(relativePathObj) };
		}
		return null;
	}
	
	private Object[] findSourceElements(String sourceName, DirectorySourceContainer containerFile) throws CoreException {
		String absolutePath = containerFile.getDirectory().getAbsolutePath();
		if (sourceName.startsWith(absolutePath)) {
			sourceName = sourceName.substring(absolutePath.length());
			return containerFile.findSourceElements(sourceName);
		} else {
			return null;
		}
	}

}
