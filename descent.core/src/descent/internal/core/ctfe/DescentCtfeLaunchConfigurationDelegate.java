package descent.internal.core.ctfe;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

import descent.core.JavaCore;
import descent.core.ctfe.IDescentCtfeLaunchConfigurationConstants;
import descent.internal.core.CompilationUnit;

public class DescentCtfeLaunchConfigurationDelegate extends LaunchConfigurationDelegate {

	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		
		String inputElementHandle = configuration.getAttribute(IDescentCtfeLaunchConfigurationConstants.ATTR_INPUT_ELEMENT_HANDLE_IDENTIFIER, (String) null);
		int inputElementSourceOffset = configuration.getAttribute(IDescentCtfeLaunchConfigurationConstants.ATTR_INPUT_ELEMENT_SOURCE_OFFSET, 0);
		CompilationUnit unit = (CompilationUnit) JavaCore.create(inputElementHandle);
		
		CtfeProcess iprocess = new CtfeProcess(launch);
		CtfeDebugger debugger = new CtfeDebugger(unit, inputElementSourceOffset, iprocess);
		
		DescentCtfeDebugTarget dbgTarget = new DescentCtfeDebugTarget(launch, iprocess, debugger);		
		launch.addDebugTarget(dbgTarget);
		
		debugger.setDebugTarget(dbgTarget);
		
		dbgTarget.started();
	}

}
