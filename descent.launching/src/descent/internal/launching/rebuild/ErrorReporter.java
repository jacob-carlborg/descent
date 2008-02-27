package descent.internal.launching.rebuild;

import descent.core.IJavaProject;

public class ErrorReporter
{
	private final IJavaProject project;
	
	public ErrorReporter(IJavaProject project)
	{
		this.project = project;
	}
}
