package descent.internal.core.ctfe;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

import descent.core.JavaCore;
import descent.core.ctfe.IDescentLaunchConfigurationConstants;
import descent.internal.core.CompilationUnit;

public class DescentLaunchConfigurationDelegate extends LaunchConfigurationDelegate {

	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		
		String inputElementHandle = configuration.getAttribute(IDescentLaunchConfigurationConstants.ATTR_INPUT_ELEMENT_HANDLE_IDENTIFIER, (String) null);
		int inputElementSourceOffset = configuration.getAttribute(IDescentLaunchConfigurationConstants.ATTR_INPUT_ELEMENT_SOURCE_OFFSET, 0);
		CompilationUnit unit = (CompilationUnit) JavaCore.create(inputElementHandle);
		
		Process iprocess = new Process(launch);
		Debugger debugger = new Debugger(unit, inputElementSourceOffset, iprocess);
		
		DescentDebugTarget dbgTarget = new DescentDebugTarget(launch, iprocess, debugger);		
		launch.addDebugTarget(dbgTarget);
		
		debugger.setDebugTarget(dbgTarget);
		
		dbgTarget.started();
	}

}
