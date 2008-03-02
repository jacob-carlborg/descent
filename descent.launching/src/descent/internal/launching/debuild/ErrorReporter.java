package descent.internal.launching.debuild;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

import descent.core.IJavaProject;
import descent.launching.compiler.BuildError;

public class ErrorReporter
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
		// TODO error atttachment
		return null;
	}
	
	public IMarker buildError(BuildError error)
	{
		// Extract this info from the error
		String msg = error.getMessage();
		String path = error.getFile();
		int line = error.getLine();
		if(null == path)
			projectError(msg);
		
		// Get the corresponding file resource
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(path));
		if(null == file || !file.exists())
			projectError(msg);
		
		// Check whether the file is a D compilation unit
		// TODO
		return resourceError(msg, file);
	}
}
