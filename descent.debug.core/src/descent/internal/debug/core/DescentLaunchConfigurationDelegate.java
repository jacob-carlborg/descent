package descent.internal.debug.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.jface.preference.IPreferenceStore;

import descent.debug.core.AbstractDescentLaunchConfigurationDelegate;
import descent.debug.core.DescentDebugPlugin;
import descent.debug.core.IDescentLaunchConfigurationConstants;
import descent.debug.core.IDescentLaunchingPreferenceConstants;
import descent.debug.core.model.IDebugger;
import descent.debug.core.utils.ArgumentUtils;
import descent.debug.core.utils.ProcessFactory;
import descent.internal.debug.core.model.DescentDebugTarget;

public class DescentLaunchConfigurationDelegate extends AbstractDescentLaunchConfigurationDelegate {
	
	public void launch(ILaunchConfiguration config, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		monitor.beginTask("Launching Local D Application", 10); //$NON-NLS-1$
		// check for cancellation
		if (monitor.isCanceled()) {
			return;
		}
		try {
			monitor.worked(1);
			IPath exePath = verifyProgramPath(config);
			verifyJavaProject(config);
			
			String[] arguments = getProgramArgumentsArray(config);

			// set the default source locator if required
			// setDefaultSourceLocator(launch, config);
			
			File wd = getWorkingDirectory(config);
			if (wd == null) {
				wd = new File(System.getProperty("user.home", ".")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			if (mode.equals(ILaunchManager.DEBUG_MODE)) {
				final IDebugger debugger = verifyDebugger();
				
				String debuggerPath = verifyDebuggerPath();
				
				ArrayList command = new ArrayList(1);
				command.add(debuggerPath);
				command.add(ArgumentUtils.toStringArgument(exePath.toOSString()));
				
				command.addAll(debugger.getDebuggerCommandLineArguments());
				
				if (arguments.length > 0) {
					command.addAll(debugger.getDebugeeCommandLineArguments(arguments));
				}
				
				String[] commandArray = (String[]) command.toArray(new String[command.size()]);
				monitor.worked(5);
				Process process = exec(commandArray, getEnvironment(config), wd);
				monitor.worked(3);
				
				// Launch the process but wait until the first end communication string
				// to proceed with the real launching
				final IPreferenceStore preferenceStore = DescentDebugPlugin.getDefault().getPreferenceStore();
				final int timeout = preferenceStore.getInt(IDescentLaunchingPreferenceConstants.DEBUGGER_TIMEOUT);
				final Object lock = new Object();
				final IProcess iprocess = DebugPlugin.newProcess(launch, process, renderProcessLabel(commandArray[1]));
				iprocess.getStreamsProxy().getOutputStreamMonitor().addListener(new IStreamListener() {

					public void streamAppended(String text, IStreamMonitor monitor) {
						if (text.trim().equals(debugger.getEndCommunicationString())) {
							iprocess.getStreamsProxy().getOutputStreamMonitor().removeListener(this);
							synchronized(lock) {
								lock.notify();
							}
						}
					}
					
				});
				
				synchronized(lock) {
					try {
						lock.wait(timeout);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				DescentDebugTarget target = new DescentDebugTarget(launch, iprocess, debugger);		
				launch.addDebugTarget(target);
				
				target.started();
			} else {
				ArrayList command = new ArrayList(1 + arguments.length);
				command.add(exePath.toOSString());
				command.addAll(Arrays.asList(arguments));
				String[] commandArray = (String[]) command.toArray(new String[command.size()]);
				monitor.worked(5);
				Process process = exec(commandArray, getEnvironment(config), wd);
				monitor.worked(3);
				DebugPlugin.newProcess(launch, process, renderProcessLabel(commandArray[0]));
			}
		} finally {
			monitor.done();
		}
	}
	
	/**
	 * Performs a runtime exec on the given command line in the context of the
	 * specified working directory, and returns the resulting process. If the
	 * current runtime does not support the specification of a working
	 * directory, the status handler for error code
	 * <code>ERR_WORKING_DIRECTORY_NOT_SUPPORTED</code> is queried to see if
	 * the exec should be re-executed without specifying a working directory.
	 * 
	 * @param cmdLine
	 *            the command line
	 * @param workingDirectory
	 *            the working directory, or <code>null</code>
	 * @return the resulting process or <code>null</code> if the exec is
	 *         cancelled
	 * @see Runtime
	 */
	protected Process exec(String[] cmdLine, String[] environ, File workingDirectory) throws CoreException {
		Process p = null;
		try {
			if (workingDirectory == null) {
				p = ProcessFactory.getFactory().exec(cmdLine, environ);
			} else {
				p = ProcessFactory.getFactory().exec(cmdLine, environ, workingDirectory);
			}
		} catch (IOException e) {
			if (p != null) {
				p.destroy();
			}
			abort("Error starting process", e, //$NON-NLS-1$
					IDescentLaunchConfigurationConstants.ERR_INTERNAL_ERROR);
		} catch (NoSuchMethodError e) {
			//attempting launches on 1.2.* - no ability to set working
			// directory

			IStatus status = new Status(IStatus.ERROR, getPluginID(),
					IDescentLaunchConfigurationConstants.ERR_WORKING_DIRECTORY_NOT_SUPPORTED, "Eclipse runtime does not support working directory", //$NON-NLS-1$
					e);
			IStatusHandler handler = DebugPlugin.getDefault().getStatusHandler(status);

			if (handler != null) {
				Object result = handler.handleStatus(status, this);
				if (result instanceof Boolean && ((Boolean) result).booleanValue()) {
					p = exec(cmdLine, environ, null);
				}
			}
		}
		return p;
	}

	protected String getPluginID() {
		return DescentDebugPlugin.PLUGIN_ID;
	}

}
