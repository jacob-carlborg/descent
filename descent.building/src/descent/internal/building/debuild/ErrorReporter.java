package descent.internal.building.debuild;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;

import descent.building.compiler.IErrorReporter;
import descent.core.IJavaProject;

// TODO ensure it's thread safe
/* package */ class ErrorReporter implements IErrorReporter
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
