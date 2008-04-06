package descent.internal.debug.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import descent.core.IJavaProject;
import descent.debug.core.AbstractDescentLaunchConfigurationDelegate;
import descent.debug.core.DebugExecutableTarget;
import descent.debug.core.DescentDebugPlugin;
public class DescentLaunchConfigurationDelegate extends AbstractDescentLaunchConfigurationDelegate {
	
	@Override
	public void launch(ILaunchConfiguration config, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException
    {
        DebugExecutableTarget target = new DebugExecutableTarget();
        
        IJavaProject project = verifyJavaProject(config);
        target.setProject(project);
        
        target.addModule("sss.main");
        
		super.launchExecutableTarget(config, target, mode, launch, monitor);
	}
	
	protected String getPluginID() {
		return DescentDebugPlugin.PLUGIN_ID;
	}
}
