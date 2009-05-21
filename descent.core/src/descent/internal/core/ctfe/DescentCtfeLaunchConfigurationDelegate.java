package descent.internal.core.ctfe;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

public class DescentCtfeLaunchConfigurationDelegate extends LaunchConfigurationDelegate {

	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		
		CtfeProcess iprocess = new CtfeProcess(launch);
		
		DescentCtfeDebugTarget dbgTarget = new DescentCtfeDebugTarget(launch, iprocess);		
		launch.addDebugTarget(dbgTarget);
	}

}
