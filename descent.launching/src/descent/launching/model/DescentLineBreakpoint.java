package descent.launching.model;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.LineBreakpoint;

import descent.launching.IDescentLaunchConfigurationConstants;

public class DescentLineBreakpoint extends LineBreakpoint {
	
	public DescentLineBreakpoint() {
	}
	
	public DescentLineBreakpoint(final IResource resource, final int lineNumber, final int charStart, final int charEnd) throws CoreException {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource
						.createMarker("descent.launching.descentLineBreakpointMarker");
				setMarker(marker);
				marker.setAttribute(IBreakpoint.ID, getModelIdentifier());
				marker.setAttribute(IBreakpoint.ENABLED, Boolean.TRUE);
				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
				marker.setAttribute(IMarker.CHAR_START, charStart);
				marker.setAttribute(IMarker.CHAR_END, charEnd);
				marker.setAttribute(IMarker.MESSAGE, "Line Breakpoint: "
						+ resource.getName() + " [line: " + lineNumber + "]");
			}
		};
		run(getMarkerRule(resource), runnable);
	}

	public String getModelIdentifier() {
		return IDescentLaunchConfigurationConstants.ID_D_DEBUG_MODEL;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}

}
