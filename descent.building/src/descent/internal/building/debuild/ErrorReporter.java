package descent.internal.building.debuild;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

import descent.core.IJavaProject;
import descent.building.compiler.BuildError;

/* package */ class ErrorReporter
{
	private final IJavaProject project;
	
	public ErrorReporter(IJavaProject project)
	{
		this.project = project;
	}
	
	public IMarker projectError(String msg)
	{
		return resourceError(msg, project.getProject());
	}
	
	public IMarker resourceError(String msg, IResource resource)
	{
        // TODO
        System.out.println(msg);
		return null;
	}
	
	public IMarker buildError(BuildError error)
	{
		// Extract this info from the error
		String msg = error.getMessage();
		String path = error.getFile();
		int line = error.getLine();
		if(null == path)
			return projectError(msg);
		
		// Get the corresponding file resource
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(path));
		if(null == file || !file.exists())
		{
			if(line <= 0)
				return projectError(String.format("%1$s: %2$s", file, msg));
			else
				return projectError(String.format("%1$s (%2$d): %3$s", file, line, msg));
		}
		
		// PERHAPS should we check if the file is a D compilation unit?
		IMarker marker = resourceError(msg, file);
		
		// TODO attach the line here
		return marker;
	}
}
