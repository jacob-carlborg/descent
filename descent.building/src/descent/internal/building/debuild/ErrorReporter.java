package descent.internal.building.debuild;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;

import descent.core.IJavaProject;

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
}
